package app.lobby;

import app.game_sessions.service.GameSessionService;
import app.lobby.model.Lobby;
import app.lobby.service.LobbyService;
import app.user.service.UserService;
import app.web.dto.PlayerRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LobbyServiceUTest {

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LobbyService lobbyService;

    @ParameterizedTest
    @MethodSource("provideLobbyOwnerInfo")
    void givenHappyFlow_createLobby(String ownerUserId, String ownerUsername) {

        Lobby lobby = lobbyService.createLobby(ownerUserId, ownerUsername);
        assertNotNull(lobby.getLobbyId());
        assertEquals(ownerUsername, lobby.getOwner().getUsername());

        List<PlayerRequest> players = lobby.getPlayers();
        assertEquals(1, players.size());
        assertEquals(ownerUsername, players.get(0).getUsername());
        assertEquals(ownerUserId, players.get(0).getUserId());
    }

    private static Stream<Arguments> provideLobbyOwnerInfo() {

        return Stream.of(
                Arguments.of("user-1", "Alibeb"),
                Arguments.of("user-2", "Magumartiel")
        );
    }

//    @ParameterizedTest
//        @MethodSource("provideJoinLobbyInfo")
//        void givenHappyAndUnhappyFlows_joinLobby(String testCase, String joiningUserId,
//                                                 String joiningUsername, boolean expectedResult) {
//
//            Lobby lobby = lobbyService.createLobby("user-1", "Alibeb");
//            String lobbyId = lobby.getLobbyId();
//
//            if ("full".equals(testCase)) {
//                boolean firstJoin = lobbyService.joinLobby(lobbyId, "user-2", "Magumartiel");
//                assertTrue(firstJoin);
//            }
//
//            boolean result = lobbyService.joinLobby(lobbyId, joiningUserId, joiningUsername);
//            assertEquals(expectedResult, result);
//
//            if (expectedResult) {
//                boolean playerExists = lobby.getPlayers().stream()
//                        .anyMatch(p -> p.getUserId().equals(joiningUserId) &&
//                                                   p.getUsername().equals(joiningUsername));
//                assertTrue(playerExists);
//            }
//
//        }
//
//        private static Stream<Arguments> provideJoinLobbyInfo() {
//
//            return Stream.of(
//                // "normal" case    -> Lobby owner and then someone else
//                Arguments.of("normal", "user-3", "Janarstitel", true),
//                // "duplicate" case -> Lobby owner, then the same user twice
//                Arguments.of("normal", "user-3", "Janarstitel", true),
//                // "full" case      -> Full lobby, meaning third user should not be able to join
//                Arguments.of("full", "user-5", "Dewisant", false)
//            );
//        }

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
}
