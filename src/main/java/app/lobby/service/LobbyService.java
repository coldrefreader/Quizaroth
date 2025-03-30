package app.lobby.service;

import app.game_sessions.service.GameSessionService;
import app.lobby.model.Lobby;
import app.user.service.UserService;
import app.web.dto.PlayerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LobbyService {

    private final Map<String, Lobby> activeLobbies = new ConcurrentHashMap<>();
    private final GameSessionService gameSessionService;
    private final UserService userService;

    @Autowired
    public LobbyService(GameSessionService gameSessionService, UserService userService) {
        this.gameSessionService = gameSessionService;
        this.userService = userService;
    }


    public Lobby createLobby(String ownerUsername, String ownerUserId) {

        String lobbyId = UUID.randomUUID().toString();
        PlayerRequest owner = new PlayerRequest(ownerUsername, ownerUserId);
        Lobby newLobby = new Lobby(lobbyId, ownerUsername, new ArrayList<>(List.of(owner)));
        activeLobbies.put(lobbyId, newLobby);
        return newLobby;
    }

    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(activeLobbies.values());
    }

    public boolean joinLobby(String lobbyId, String username, String userId) {
        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby != null && !lobby.isFull()) {

            PlayerRequest newPlayer = new PlayerRequest(userId, username);
            if (!lobby.getPlayers().contains(newPlayer)) {
                lobby.getPlayers().add(newPlayer);
                log.info("Added {} to lobby {}", username, lobbyId);
            } else {
                log.info("User {} already in the lobby {}", username, lobbyId);
            }
            return true;
        }
        return false;
    }

    public boolean disbandLobby(String lobbyId, String username) {

        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby == null) {
            log.warn("Lobby {} not found in active lobbies: {}", lobbyId, activeLobbies.keySet());
            return false;
        }
        log.info("Attempting to disband lobby: {}, Owner: {}, Requested by: {}",
                lobbyId, lobby.getOwner(), username);
        if (lobby.getOwner().equals(username)) {
            activeLobbies.remove(lobbyId);
            log.info("Lobby {} has been disbanded by owner {}", lobbyId, username);
            return true;
        } else {
            log.warn("User {} is not the owner of the lobby {}", username, lobbyId);
        }
        return false;
    }

    public boolean startGame(String lobbyId, String username) {

        Lobby lobby = activeLobbies.get(lobbyId);

        if (lobby == null || !lobby.getOwner().equals(username) || !lobby.isFull()) return false;

        try {

            PlayerRequest player1 = lobby.getPlayers().get(0);
            PlayerRequest player2 = lobby.getPlayers().get(1);

            UUID player1Id = UUID.fromString(player1.getUserId());
            UUID player2Id = UUID.fromString(player2.getUserId());

            gameSessionService.createGameSession(player1Id, player2Id);
            log.info("Starting game session for lobby " + lobbyId + " with user " + username);

            activeLobbies.remove(lobbyId);
            return true;
        } catch (Exception e) {
            log.error("Failed to start game for lobby: {}", lobbyId, e);
            return false;
        }
    }

    public boolean leaveLobby(String lobbyId, String username) {

        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby == null) return false;

        return lobby.getPlayers().removeIf(player -> player.getUserName().equals(username));
    }

    public Lobby getLobbyById(String lobbyId) {
        return activeLobbies.get(lobbyId);
    }
}
