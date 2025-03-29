package app.lobby.model;

import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lobby {

    private String lobbyId;
    private String owner;
    private List<String> players = Collections.synchronizedList(new ArrayList<>());

    public boolean isFull() {
        return players.size() >= 2;
    }

    public boolean containsPlayer(String username) {
        return players.contains(username);
    }
}
