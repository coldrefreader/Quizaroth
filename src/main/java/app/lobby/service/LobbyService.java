package app.lobby.service;

import app.game_sessions.service.GameSessionService;
import app.lobby.model.Lobby;
import app.user.service.UserService;
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


    public Lobby createLobby(String owner) {
        String lobbyId = UUID.randomUUID().toString();
        Lobby newLobby = new Lobby(lobbyId, owner, new ArrayList<>(List.of(owner)));
        activeLobbies.put(lobbyId, newLobby);
        return newLobby;
    }

    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(activeLobbies.values());
    }

    public boolean joinLobby(String lobbyId, String username) {
        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().add(username);
            return true;
        }
        return false;
    }

    public boolean disbandLobby(String lobbyId, String username) {
        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby != null && lobby.getOwner().equals(username)) {
            activeLobbies.remove(lobbyId);
            return true;
        }
        return false;
    }

    public boolean startGame(String lobbyId, String username) {
        Lobby lobby = activeLobbies.get(lobbyId);

        if (lobby == null || !lobby.getOwner().equals(username) || !lobby.isFull()) return false;

        try {
            UUID player1Id = userService.getUserIdByUsername(lobby.getPlayers().get(0));
            UUID player2Id = userService.getUserIdByUsername(lobby.getPlayers().get(1));

            gameSessionService.createGameSession(player1Id, player2Id);
            log.info("Starting game session for lobby " + lobbyId + " with user " + username);

            activeLobbies.remove(lobbyId);
            return true;
        } catch (Exception e) {
            log.error("Failed to start game for lobby: {}", lobbyId, e);
            return false;
        }
    }

    public Lobby getLobbyById(String lobbyId) {
        return activeLobbies.get(lobbyId);
    }
}
