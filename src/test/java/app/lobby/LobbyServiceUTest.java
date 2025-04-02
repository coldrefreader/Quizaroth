package app.lobby;

import app.game_sessions.service.GameSessionService;
import app.lobby.model.Lobby;
import app.lobby.repository.LobbyHistoryRepository;
import app.lobby.service.LobbyService;
import app.user.service.UserService;
import app.web.dto.PlayerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LobbyServiceUTest {

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private UserService userService;

    @Mock
    private LobbyHistoryRepository lobbyHistoryRepository;

    @InjectMocks
    private LobbyService lobbyService;


    @ParameterizedTest
    @MethodSource("provideLobbyOwnerInfo")
    void givenHappyFlow_createLobby(String ownerUserId, String ownerUsername) {

        List<Lobby> mockHistory = new ArrayList<>();
        when(lobbyHistoryRepository.getLobbyHistory()).thenReturn(mockHistory);

        Lobby lobby = lobbyService.createLobby(ownerUserId, ownerUsername);
        assertNotNull(lobby.getLobbyId());
        assertEquals(ownerUsername, lobby.getOwner().getUsername());

        List<PlayerRequest> players = lobby.getPlayers();
        assertEquals(1, players.size());
        assertEquals(ownerUsername, players.get(0).getUsername());
        assertEquals(ownerUserId, players.get(0).getUserId());

        mockHistory.add(lobby);

        List<Lobby> history = lobbyHistoryRepository.getLobbyHistory();
        boolean found = history.stream()
                            .anyMatch(historyLobby -> historyLobby.getLobbyId().equals(lobby.getLobbyId()) &&
                                                historyLobby.getOwner().getUsername().equals(ownerUsername) &&
                                                historyLobby.getOwner().getUserId().equals(ownerUserId));
        assertTrue(found);
    }

    private static Stream<Arguments> provideLobbyOwnerInfo() {

        return Stream.of(
                Arguments.of("user-1", "Alibeb"),
                Arguments.of("user-2", "Magumartiel")
        );
    }


    @ParameterizedTest
    @MethodSource("provideJoinLobbyNormalData")
    void givenHappyFlow_joinLobby(String ownerUserId, String ownerUsername,
                                  String joiningUserId, String joiningUsername) {

        Lobby lobby = lobbyService.createLobby(ownerUserId, ownerUsername);
        String lobbyId = lobby.getLobbyId();

        boolean resultFirstJoin = lobbyService.joinLobby(lobbyId, joiningUserId, joiningUsername);
        assertTrue(resultFirstJoin, "First join should succeed");

        boolean playerIsPresent = lobby.getPlayers().stream()
                .anyMatch(p -> p.getUserId().equals(joiningUserId) &&
                                           p.getUsername().equals(joiningUsername));
        assertTrue(playerIsPresent, "Player should be present");

    }

    private static Stream<Arguments> provideJoinLobbyNormalData() {
        return Stream.of(
                Arguments.of("user-1", "Alibeb", "user-2", "Janarstitel")
        );
    }


    @ParameterizedTest
    @MethodSource("provideJoinLobbyDuplicateData")
    void givenDuplicateJoin_returnOneCountOfPlayer(String lobbyOwnerId, String lobbyOwnerUsername,
                                                   String joiningUserId, String joiningUsername) {

        Lobby lobby = lobbyService.createLobby(lobbyOwnerId, lobbyOwnerUsername);
        String lobbyId = lobby.getLobbyId();

        boolean firstJoinResult = lobbyService.joinLobby(lobbyId, joiningUserId, joiningUsername);
        assertTrue(firstJoinResult, "First join should succeed");

        boolean secondJoinResult = lobbyService.joinLobby(lobbyId, joiningUserId, joiningUsername);
        assertTrue(secondJoinResult, "Second join returns true, however it does not add the user again");

        long count = lobby.getPlayers().stream()
                .filter(player -> player.getUserId().equals(joiningUserId) &&
                                              player.getUsername().equals(joiningUsername))
                .count();
        assertEquals(1, count, "Duplicate join should not add another instance of the same user");

    }

    private static Stream<Arguments> provideJoinLobbyDuplicateData() {
        return Stream.of(
                Arguments.of("user-1", "Alibeb", "user-2", "Janarstitel")
        );
    }


    @Test
    void givenFakeLobby_whenJoinLobby_thenReturnFalse() {

        String fakeLobbyId = "fakeLobbyId";
        boolean result = lobbyService.joinLobby(fakeLobbyId, "fakeUserId", "fakeUsername");
        assertFalse(result);
    }


    @Test
    void givenFullLobby_whenJoinLobby_thenReturnFalse() {

        Lobby lobby = lobbyService.createLobby("user-1", "Alibeb");
        String lobbyId = lobby.getLobbyId();

        boolean joinSecond = lobbyService.joinLobby(lobbyId, "user-2", "Janarstitel");
        assertTrue(joinSecond, "First join should succeed");

        boolean joinThird = lobbyService.joinLobby(lobbyId, "user-3", "Magumartiel");
        assertFalse(joinThird, "Second join should fail");
    }


    @Test
    void givenFakeLobby_whenDisbandLobby_thenReturnFalse() {

        String fakeLobbyId = "fakeLobbyId";
        boolean result = lobbyService.disbandLobby(fakeLobbyId, "fakeUserId");
        assertFalse(result, "Disbanding a fake lobby should return false");
    }

    @Test
    void givenNonOwner_whenDisbandLobby_thenReturnFalse() {

        String ownerUserId = "ownerUserId";
        String ownerUsername = "Magumartiel";

        Lobby lobby = lobbyService.createLobby(ownerUserId, ownerUsername);
        String lobbyId = lobby.getLobbyId();

        String nonOwnerUsername = "Belial";
        boolean result = lobbyService.disbandLobby(lobbyId, nonOwnerUsername);
        assertFalse(result, "Non-owners do not have the permission to disband a lobby");

        assertNotNull(lobbyService.getLobbyById(lobbyId));
    }

    @Test
    void givenHappyFlow_whenDisbandLobby_thenReturnTrue() {

        String ownerUserId = "ownerUserId";
        String ownerUsername = "ownerUsername";

        Lobby lobby = lobbyService.createLobby(ownerUserId, ownerUsername);
        String lobbyId = lobby.getLobbyId();

        assertEquals(ownerUsername, lobby.getOwner().getUsername());

        boolean result = lobbyService.disbandLobby(lobbyId, ownerUsername);
        assertTrue(result, "The owner of a lobby disbanding it should return true");

        assertNull(lobbyService.getLobbyById(lobbyId), "The lobby should no longer be found");
    }
}
