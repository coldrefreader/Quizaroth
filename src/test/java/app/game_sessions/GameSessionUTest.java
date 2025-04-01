package app.game_sessions;

import app.exception.DomainException;
import app.game_sessions.model.GameResult;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.game_sessions.service.GameSessionService;
import app.leaderboard.service.LeaderboardService;
import app.user.model.User;
import app.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameSessionUTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private GameSessionService gameSessionService;


    @Test
    void givenHappyFlow_createGameSession() {

        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();
        User player1 = User.builder().id(player1Id).build();
        User player2 = User.builder().id(player2Id).build();

        when(userRepository.findById(player1Id)).thenReturn(Optional.of(player1));
        when(userRepository.findById(player2Id)).thenReturn(Optional.of(player2));

        GameSession gameSession = GameSession.builder()
                .player1(player1)
                .player2(player2)
                .player1Score(0)
                .player2Score(0)
                .result(GameResult.UNDETERMINED)
                .timestamp(LocalDateTime.now())
                .build();

        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(gameSession);

        GameSession result = gameSessionService.createGameSession(player1Id, player2Id);

        assertNotNull(result);
        assertEquals(player1, gameSession.getPlayer1());
        assertEquals(player2, gameSession.getPlayer2());
        assertEquals(0, gameSession.getPlayer1Score());
        assertEquals(0, gameSession.getPlayer2Score());
        assertEquals(GameResult.UNDETERMINED, result.getResult());
        assertNotNull(result.getTimestamp());

        verify(userRepository, times(1)).findById(player1Id);
        verify(userRepository, times(1)).findById(player2Id);
        verify(gameSessionRepository, times(1)).save(any(GameSession.class));
    }

    @Test
    void givenOneUserIdIsMissing_whenCreateGameSession_thenThrowDomainException() {

        UUID correctPlayerId = UUID.randomUUID();
        UUID missingPlayerId = UUID.randomUUID();
        User existingPlayer = User.builder().id(correctPlayerId).build();

        when(userRepository.findById(correctPlayerId)).thenReturn(Optional.of(existingPlayer));
        when(userRepository.findById(missingPlayerId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class, () ->
                gameSessionService.createGameSession(correctPlayerId, missingPlayerId));
        //"User with id " + player2Id + " not found"));
        assertEquals("User with id " + missingPlayerId + " not found", exception.getMessage());

        verify(userRepository, times(1)).findById(correctPlayerId);
        verify(userRepository, times(1)).findById(missingPlayerId);
        verify(gameSessionRepository, never()).save(any(GameSession.class));
    }
}
