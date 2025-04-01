package app.game_sessions;

import app.TestBuilder;
import app.exception.DomainException;
import app.game_sessions.model.GameResult;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.game_sessions.service.GameSessionService;
import app.leaderboard.service.LeaderboardService;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.web.dto.FinaliseGameStateResponse;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class GameSession2UTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private GameSessionService gameSessionService;

    private UUID sessionId;
    private GameSession gameSession;
    private User player1;
    private User player2;

    @BeforeEach
    void setUp() {

        sessionId = UUID.randomUUID();
        player1 = TestBuilder.newUser();
        player2 = TestBuilder.newUser2();

        gameSession = GameSession.builder()
                .id(sessionId)
                .player1(player1)
                .player2(player2)
                .player1Score(0)
                .player2Score(0)
                .result(GameResult.UNDETERMINED)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void givenAlreadyFinalisedGame_whenFinaliseGame_thenLogWarningAndStopMethod() {

        gameSession.setResult(GameResult.VICTORY);
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.of(gameSession));

        FinaliseGameStateResponse finalState = new FinaliseGameStateResponse();
        Map<String, Integer> scores = new HashMap<>();
        scores.put("player1", 3);
        scores.put("player2", 5);
        finalState.setPlayerScores(scores);

        LogCaptor logCaptor = LogCaptor.forClass(GameSessionService.class);

        gameSessionService.finaliseGame(sessionId, finalState);

        assertFalse(logCaptor.getWarnLogs().isEmpty(), "No warning logs found");
        assertTrue(logCaptor.getWarnLogs().get(0)
                .contains("Game session " + sessionId + " already finalised with result: " + GameResult.VICTORY));


        verify(gameSessionRepository, never()).save(any(GameSession.class));
        verify(leaderboardService, never()).updateLeaderboard(anyString(), anyBoolean());
    }

    @Test
    void givenExistingGameSession_whenGetGameSession_thenReturnGameSession() {

        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.of(gameSession));

        GameSession retrievedGameSession = gameSessionService.getGameSessionById(sessionId);
        assertNotNull(retrievedGameSession);
        assertEquals(sessionId, retrievedGameSession.getId());
    }

    @Test
    void givenEmptyGameSession_whenGetGameSession_thenThrowException() {

        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class, () -> {
            gameSessionService.getGameSessionById(sessionId);
        });
        assertTrue(exception.getMessage().contains(("Game session with id " + sessionId + " not found")));
    }
}
