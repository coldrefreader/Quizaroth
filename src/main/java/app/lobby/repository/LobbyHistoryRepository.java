package app.lobby.repository;

import app.lobby.model.Lobby;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class LobbyHistoryRepository {

    private final List<Lobby> lobbyHistory = new ArrayList<>();

    public void addLobby(Lobby lobby) {
        synchronized (lobbyHistory) {
            lobbyHistory.add(lobby);
        }
    }

    public List<Lobby> getLobbyHistory() {
        synchronized (lobbyHistory) {
            return Collections.unmodifiableList(new ArrayList<>(lobbyHistory));
        }
    }
}
