package app.match_history;

import app.game_sessions.model.GameResult;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.match_history.service.MatchHistoryService;
import app.user.model.User;
import app.web.dto.MatchHistoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchHistoryUTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @InjectMocks
    private MatchHistoryService matchHistoryService;

    private UUID userId;
    private GameSession gameSession1;
    private GameSession gameSession2;
    private GameSession gameSessionDraw;
    private GameSession gameSessionPlayer2Defeat;
    private GameSession gameSessionPlayer2Draw;
    private LocalDateTime fixedTimeStamp;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        fixedTimeStamp = LocalDateTime.now().withNano(0);

        User player1 = new User();
        player1.setId(userId);
        player1.setUsername("player1");

        User opponent1 = new User();
        opponent1.setId(UUID.randomUUID());
        opponent1.setUsername("opponent1");

        User opponent2 = new User();
        opponent2.setId(UUID.randomUUID());
        opponent2.setUsername("opponent2");

        gameSession1 = GameSession.builder()
                .player1(player1)
                .player2(opponent1)
                .player1Score(9)
                .player2Score(7)
                .result(GameResult.VICTORY)
                .timestamp(fixedTimeStamp)
                .build();

        gameSession2 = GameSession.builder()
                .player1(opponent2)
                .player2(player1)
                .player1Score(5)
                .player2Score(3)
                .result(GameResult.VICTORY)
                .timestamp(fixedTimeStamp.plusMinutes(1))
                .build();

        gameSessionDraw = GameSession.builder()
                .player1(player1)
                .player2(opponent1)
                .player1Score(2)
                .player2Score(2)
                .result(GameResult.DRAW)
                .timestamp(fixedTimeStamp.plusMinutes(2))
                .build();

        gameSessionPlayer2Defeat = GameSession.builder()
                .player1(opponent1)
                .player2(player1)
                .player1Score(5)
                .player2Score(3)
                .result(GameResult.DEFEAT)
                .timestamp(fixedTimeStamp.plusMinutes(3))
                .build();

        gameSessionPlayer2Draw = GameSession.builder()
                .player1(opponent2)
                .player2(player1)
                .player1Score(2)
                .player2Score(2)
                .result(GameResult.DRAW)
                .timestamp(fixedTimeStamp.plusMinutes(4))
                .build();

        when(gameSessionRepository.findTop10ByPlayer1IdOrPlayer2IdOrderByTimestampDesc(userId, userId))
                .thenReturn(List.of(gameSession1, gameSession2, gameSessionDraw, gameSessionPlayer2Defeat, gameSessionPlayer2Draw));


    }

    @Test
    void givenHappyFlowVictory_whenGetMatchHistory_thenReturnMatchHistory() {

        List<MatchHistoryRequest> matchHistory = matchHistoryService.getMatchHistory(userId);

        MatchHistoryRequest history1 = matchHistory.get(0);
        assertEquals(gameSession1.getId(), history1.getGameSessionId());
        assertEquals("opponent1", history1.getOpponentUsername());
        assertEquals(9, history1.getPlayerScore());
        assertEquals(7, history1.getOpponentScore());
        assertEquals(GameResult.VICTORY.toString(), history1.getResult());
        assertEquals(fixedTimeStamp, history1.getTimestamp());

        MatchHistoryRequest history2 = matchHistory.get(3);
        assertEquals(gameSessionPlayer2Defeat.getId(), history2.getGameSessionId());
        assertEquals("opponent1", history2.getOpponentUsername());
        assertEquals(3, history2.getPlayerScore());
        assertEquals(5, history2.getOpponentScore());
        assertEquals(GameResult.VICTORY.toString(), history2.getResult());
        assertEquals(fixedTimeStamp.plusMinutes(3), history2.getTimestamp());
    }

    @Test
    void givenHappyFlowDefeat_whenGetMatchHistory_thenReturnMatchHistory() {

        List<MatchHistoryRequest> matchHistory = matchHistoryService.getMatchHistory(userId);

        MatchHistoryRequest history3 = matchHistory.get(1);
        assertEquals(gameSession2.getId(), history3.getGameSessionId());
        assertEquals("opponent2", history3.getOpponentUsername());
        assertEquals(3, history3.getPlayerScore());
        assertEquals(5, history3.getOpponentScore());
        assertEquals(GameResult.DEFEAT.toString(), history3.getResult());
        assertEquals(fixedTimeStamp.plusMinutes(1), history3.getTimestamp());
    }

    @Test
    void givenHappyFlowDraw_whenGetMatchHistory_thenReturnMatchHistory() {

        List<MatchHistoryRequest> matchHistory = matchHistoryService.getMatchHistory(userId);

        MatchHistoryRequest history4 = matchHistory.get(2);
        assertEquals(gameSessionDraw.getId(), history4.getGameSessionId());
        assertEquals("opponent1", history4.getOpponentUsername());
        assertEquals(2, history4.getPlayerScore());
        assertEquals(2, history4.getOpponentScore());
        assertEquals(GameResult.DRAW.toString(), history4.getResult());
        assertEquals(fixedTimeStamp.plusMinutes(2), history4.getTimestamp());

        MatchHistoryRequest history5 = matchHistory.get(4);
        assertEquals(gameSessionPlayer2Draw.getId(), history5.getGameSessionId());
        assertEquals("opponent2", history5.getOpponentUsername());
        assertEquals(2, history5.getPlayerScore());
        assertEquals(2, history5.getOpponentScore());
        assertEquals(GameResult.DRAW.toString(), history5.getResult());
        assertEquals(fixedTimeStamp.plusMinutes(4), history5.getTimestamp());
    }

    @Test
    void givenEmptyMatchHistory_whenGetMatchHistory_thenReturnEmptyList() {

        when(gameSessionRepository.findTop10ByPlayer1IdOrPlayer2IdOrderByTimestampDesc(userId, userId))
                .thenReturn(List.of());

        List<MatchHistoryRequest> matchHistory = matchHistoryService.getMatchHistory(userId);
        assertTrue(matchHistory.isEmpty());
    }

    @Test
    void givenMatchHistoryList_whenGetMatchHistory_thenReturnMatchHistorySize() {

        List<MatchHistoryRequest> matchHistory = matchHistoryService.getMatchHistory(userId);

        assertEquals(5, matchHistory.size());
    }
}
