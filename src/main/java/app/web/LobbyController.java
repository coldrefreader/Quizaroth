package app.web;

import app.lobby.model.Lobby;
import app.lobby.service.LobbyService;
import app.security.AuthenticationMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/lobbies")
public class LobbyController {

    private final LobbyService lobbyService;

    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/create")
    public ResponseEntity<Lobby> createLobby(@AuthenticationPrincipal AuthenticationMetadata user) {
        return ResponseEntity.ok(lobbyService.createLobby(user.getUsername(), user.getUserId().toString()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Lobby>> listLobbies() {
        return ResponseEntity.ok(lobbyService.getAllLobbies());
    }

    @GetMapping("/{lobbyId}")
    public ResponseEntity<Lobby> getLobby(@PathVariable String lobbyId) {
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        return ResponseEntity.ok(lobby);
    }

    @PostMapping("/join/{lobbyId}")
    public ResponseEntity<String> joinLobby(@PathVariable String lobbyId, @AuthenticationPrincipal AuthenticationMetadata user) {
        boolean success = lobbyService.joinLobby(lobbyId, user.getUsername(), user.getUserId().toString());
        return success ? ResponseEntity.ok("Joined lobby") : ResponseEntity.status(400).body("Lobby is full or does not exist");
    }

    @DeleteMapping("/leave/{lobbyId}")
    public ResponseEntity<String> leaveLobby(@PathVariable String lobbyId, @AuthenticationPrincipal AuthenticationMetadata user) {
        boolean success = lobbyService.leaveLobby(lobbyId, user.getUsername());
        return success ? ResponseEntity.ok("Left lobby") : ResponseEntity.status(403).body("Failed to leave the lobby");
    }

    @DeleteMapping("/disband/{lobbyId}")
    public ResponseEntity<String> disbandLobby(@PathVariable String lobbyId, @AuthenticationPrincipal AuthenticationMetadata user) {
        boolean success = lobbyService.disbandLobby(lobbyId, user.getUsername());
        return success ? ResponseEntity.ok("Lobby disbanded") : ResponseEntity.status(403).body("Only the owner can disband the lobby");
    }

    @PostMapping("/start/{lobbyId}")
    public ResponseEntity<String> startGame(@PathVariable String lobbyId, @AuthenticationPrincipal AuthenticationMetadata user) {
        boolean success = lobbyService.startGame(lobbyId, user.getUsername());
        return success ? ResponseEntity.ok("Game session started") : ResponseEntity.status(400).body("Only the owner can start and the lobby must be full");
    }
}
