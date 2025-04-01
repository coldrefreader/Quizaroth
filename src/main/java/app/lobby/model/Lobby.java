package app.lobby.model;

import app.web.dto.PlayerRequest;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lobby {

    private String lobbyId;
    private PlayerRequest owner;
    private List<PlayerRequest> players;

    public boolean isFull() {
        return players.size() >= 2;
    }
}
