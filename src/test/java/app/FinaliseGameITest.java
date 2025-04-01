package app;

import app.game_sessions.model.GameResult;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.game_sessions.service.GameSessionService;
import app.leaderboard.service.LeaderboardService;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.web.dto.FinaliseGameStateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest

public class FinaliseGameITest {

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private LeaderboardService leaderboardService;

    private User player1;
    private User player2;

    @BeforeEach
    void setup() {

        player1 = userRepository.save(TestBuilder.newUser());
        player2 = userRepository.save(TestBuilder.newUser2());
    }

    @ParameterizedTest
    @MethodSource("finaliseGameArguments")
    void finaliseGameAllResults(int finalPlayer1Score, int finalPlayer2Score, GameResult expectedResult) {

        GameSession gameSession = gameSessionService.createGameSession(player1.getId(), player2.getId());
        UUID sessionId = gameSession.getId();

        FinaliseGameStateResponse finalState = FinaliseGameStateResponse.builder()
                .playerScores(Map.of(
                        player1.getUsername(), finalPlayer1Score,
                        player2.getUsername(), finalPlayer2Score))
                .build();

        gameSessionService.finaliseGame(sessionId, finalState);

        GameSession updatedSession = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found"));

        assertEquals(finalPlayer1Score, updatedSession.getPlayer1Score());
        assertEquals(finalPlayer2Score, updatedSession.getPlayer2Score());
        assertEquals(expectedResult, updatedSession.getResult());

    }

    private static Stream<Arguments> finaliseGameArguments() {

        return Stream.of(
                Arguments.of(7, 3, GameResult.VICTORY),
                Arguments.of(3, 5, GameResult.DEFEAT),
                Arguments.of(1, 1, GameResult.DRAW)
        );
    }
}
