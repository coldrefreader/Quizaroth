package app.lobby.service;

import app.game_sessions.service.GameSessionService;
import app.lobby.model.Lobby;
import app.lobby.repository.LobbyHistoryRepository;
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
    private final LobbyHistoryRepository lobbyHistoryRepository;

    @Autowired
    public LobbyService(GameSessionService gameSessionService, UserService userService, LobbyHistoryRepository lobbyHistoryRepository) {
        this.gameSessionService = gameSessionService;
        this.userService = userService;
        this.lobbyHistoryRepository = lobbyHistoryRepository;
    }


    public Lobby createLobby(String ownerUserId, String ownerUsername) {

        String lobbyId = UUID.randomUUID().toString();
        PlayerRequest owner = PlayerRequest.builder()
                .userId(ownerUserId)
                .username(ownerUsername)
                .build();
        Lobby newLobby = new Lobby(lobbyId, owner, new ArrayList<>(List.of(owner)));
        activeLobbies.put(lobbyId, newLobby);
        lobbyHistoryRepository.addLobby(newLobby);
        log.info("Created lobby {} with owner {}", lobbyId, owner);
        log.info("Full lobby info: {}", newLobby);
        return newLobby;
    }

    public List<Lobby> getAllLobbies() {
        return new ArrayList<>(activeLobbies.values());
    }

    public boolean joinLobby(String lobbyId, String userId, String username) {

        Lobby lobby = activeLobbies.get(lobbyId);
        if (lobby != null) {
            PlayerRequest newPlayer = PlayerRequest.builder()
                    .userId(userId)
                    .username(username)
                    .build();
            if (lobby.getPlayers().contains(newPlayer)) {
                log.info("User {} already in the lobby {}", username, lobbyId);
                return true;
            }
            if (!lobby.isFull()) {
                lobby.getPlayers().add(newPlayer);
                log.info("Added {} to lobby {}", username, lobbyId);
                return true;
            }
        }
        log.info("Lobby {} not found or full", lobbyId);
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
        if (lobby.getOwner().getUsername().equals(username)) {
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

        if (lobby == null || !lobby.getOwner().getUsername().equals(username) || !lobby.isFull()) {

            log.warn("Cannot start game for lobby {}: lobby is null, owner mismatch or not full", lobbyId);
            return false;
        }

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

        boolean removed = lobby.getPlayers().removeIf(player -> player.getUsername().equals(username));

        if (removed) {
            log.info("User {} left lobby {}", username, lobbyId);
        } else {
            log.warn("User {} is not in the lobby {}", username, lobbyId);
        }
        return removed;
    }

    public Lobby getLobbyById(String lobbyId) {
        return activeLobbies.get(lobbyId);
    }
}
