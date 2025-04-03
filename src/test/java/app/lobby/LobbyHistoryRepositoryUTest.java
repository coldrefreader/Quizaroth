package app.lobby;

import app.lobby.model.Lobby;
import app.lobby.repository.LobbyHistoryRepository;
import app.web.dto.PlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LobbyHistoryRepositoryUTest {

    private LobbyHistoryRepository lobbyHistoryRepository;

    @BeforeEach
    void setUp() {
        lobbyHistoryRepository = new LobbyHistoryRepository();
    }

    @Test
    void givenCorrectLobby_whenCreateLobby_thenAddLobby() {

        PlayerRequest owner = PlayerRequest.builder()
                .userId("user-1")
                .username("Owner")
                .build();

        Lobby lobby = new Lobby("1", owner, List.of(owner));
        lobbyHistoryRepository.addLobby(lobby);

        List<Lobby> lobbyHistory = lobbyHistoryRepository.getLobbyHistory();
        assertEquals(1, lobbyHistory.size());
        assertEquals("1", lobbyHistory.get(0).getLobbyId());
    }

    @Test
    void givenCorrectLobby_whenGetLobbyHistory_thenGetLobbyHistory() {

        PlayerRequest owner = PlayerRequest.builder()
                .userId("user-1")
                .username("Owner")
                .build();

        Lobby lobby = new Lobby("1", owner, List.of(owner));

        PlayerRequest owner2 = PlayerRequest.builder()
                .userId("user-2")
                .username("Owner2")
                .build();

        Lobby lobby2 = new Lobby("2", owner2, List.of(owner));
        lobbyHistoryRepository.addLobby(lobby);
        lobbyHistoryRepository.addLobby(lobby2);

        List<Lobby> lobbyHistory = lobbyHistoryRepository.getLobbyHistory();
        assertEquals(2, lobbyHistory.size());
        assertEquals("1", lobbyHistory.get(0).getLobbyId());
        assertEquals("2", lobbyHistory.get(1).getLobbyId());
    }

    @Test
    void givenIncorrectLobby_whenGetLobbyHistory_thenThrowException() {

        PlayerRequest owner = PlayerRequest.builder()
                .userId("user-1")
                .username("Owner")
                .build();

        Lobby lobby = new Lobby("1", owner, List.of(owner));
        lobbyHistoryRepository.addLobby(lobby);

        List<Lobby> lobbyHistory = lobbyHistoryRepository.getLobbyHistory();
        assertThrows(UnsupportedOperationException.class, () -> lobbyHistory.add(new Lobby()));

    }
}
