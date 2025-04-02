package app.lobby;

import app.game_sessions.model.GameSession;
import app.game_sessions.service.GameSessionService;
import app.lobby.model.Lobby;
import app.lobby.repository.LobbyHistoryRepository;
import app.lobby.service.LobbyService;
import app.user.service.UserService;
import app.web.dto.PlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LobbyService2UTest {

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private UserService userService;

    @Mock
    private LobbyHistoryRepository lobbyHistoryRepository;

    @InjectMocks
    private LobbyService lobbyService;

    private Lobby lobby;
    private PlayerRequest owner;
    private PlayerRequest playerTwo;

    @BeforeEach
    void setUp() {

        owner = PlayerRequest.builder()
                .userId(UUID.randomUUID().toString())
                .username("host")
                .build();
        playerTwo = PlayerRequest.builder()
                .userId(UUID.randomUUID().toString())
                .username("Janarstitel")
                .build();

        lobby = lobbyService.createLobby(owner.getUserId(), owner.getUsername());
        List<PlayerRequest> players = new ArrayList<>(List.of(owner, playerTwo));
        lobby.setPlayers(players);
    }

    @Test
    void givenGetRequest_whenGetAllLobbies_thenReturnListOfLobbies() {

        boolean result = lobbyService.getAllLobbies().size() == 1;
        assertTrue(result);

        Lobby lobby2 = lobbyService.createLobby("newOwner-1", "newOwnerUsername");
        boolean result2 = lobbyService.getAllLobbies().size() == 2;
        assertTrue(result2);
    }


    @Test
    void givenFakeUser_whenLeaveLobby_thenReturnFalse() {

        boolean result = lobbyService.leaveLobby(lobby.getLobbyId(), "fakeUser");

        assertFalse(result);
        assertEquals(2, lobby.getPlayers().size());
    }

    @Test
    void givenHappyFlow_whenLeaveLobby_thenReturnTrue() {

        boolean result = lobbyService.leaveLobby(lobby.getLobbyId(), playerTwo.getUsername());

        assertTrue(result);
        assertEquals(1, lobby.getPlayers().size());
        assertFalse(lobby.getPlayers().contains(playerTwo));
    }

    @Test
    void givenFakeLobby_whenLeaveLobby_thenReturnFalse() {

        String fakeLobbyId = "fakeLobbyId";

        boolean result = lobbyService.leaveLobby(fakeLobbyId, playerTwo.getUsername());
        assertFalse(result, "Leaving a fake lobby should return false");
    }

    @Test
    void givenFakeLobby_whenStartGame_thenReturnFalse() {

        String fakeLobbyId = "fakeLobbyId";

        boolean result = lobbyService.startGame(fakeLobbyId, owner.getUsername());

        assertFalse(result, "Starting a fake lobby should return false");
        verify(gameSessionService, never()).createGameSession(any(UUID.class), any(UUID.class));
    }

    @Test
    void givenNonOwner_whenStartGame_thenReturnFalse() {

        boolean result = lobbyService.startGame(lobby.getLobbyId(), playerTwo.getUsername());

        assertFalse(result, "Non-owners should not be able to start a game");
        verify(gameSessionService, never()).createGameSession(any(UUID.class), any(UUID.class));
    }

    @Test
    void givenNotFullLobby_whenStartGame_thenReturnFalse() {

        lobbyService.leaveLobby(lobby.getLobbyId(), playerTwo.getUsername());

        boolean result = lobbyService.startGame(lobby.getLobbyId(), owner.getUsername());

        assertFalse(result, "Starting a non-full lobby should return false");
        verify(gameSessionService, never()).createGameSession(any(UUID.class), any(UUID.class));
    }

    @Test
    void givenHappyFlow_whenStartGame_thenReturnTrue() {

        GameSession gameSession = mock(GameSession.class);

        when(gameSessionService.createGameSession(any(UUID.class), any(UUID.class))).thenReturn(gameSession);

        boolean result = lobbyService.startGame(lobby.getLobbyId(), owner.getUsername());
        assertTrue(result);

        verify(gameSessionService, times(1)).createGameSession(any(UUID.class), any(UUID.class));
        assertFalse(lobbyService.getAllLobbies().contains(lobby));
    }

    @Test
    void givenException_whenStartGame_thenReturnFalse() {

        doThrow(new RuntimeException("Failed to create a new game session")).when(gameSessionService)
                .createGameSession(any(UUID.class), any(UUID.class));

        boolean result = lobbyService.startGame(lobby.getLobbyId(), owner.getUsername());
        assertFalse(result);

        verify(gameSessionService, times(1)).createGameSession(any(UUID.class), any(UUID.class));
        assertTrue(lobbyService.getAllLobbies().contains(lobby));
    }
}
