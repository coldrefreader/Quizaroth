package app.web;

import app.game_sessions.model.GameSession;
import app.game_sessions.service.GameSessionService;
import app.web.dto.FinaliseGameStateResponse;
import app.web.dto.GameSessionResponse;
import app.web.dto.GameSessionRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/game-sessions")
public class GameSessionController {

    private final GameSessionService gameSessionService;


    @Autowired
    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping
    public ResponseEntity<?> createGameSession(@Valid @RequestBody GameSessionRequest request) {

        UUID player1Id = request.getPlayer1Id();
        UUID player2Id = request.getPlayer2Id();

        GameSession gameSession = gameSessionService.createGameSession(player1Id, player2Id);

        return ResponseEntity.ok(Map.of("gameSessionId", gameSession.getId()));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<GameSessionResponse> getGameSession(@PathVariable UUID sessionId) {

        GameSession gameSession = gameSessionService.getGameSessionById(sessionId);
        GameSessionResponse request = gameSessionService.createRequest(gameSession);

        return ResponseEntity.ok(request);
    }

    @PostMapping("/finalise/{sessionId}")
    public ResponseEntity<?> finaliseGameSession(@PathVariable UUID sessionId,
                                                 @Valid @RequestBody FinaliseGameStateResponse response) {

        gameSessionService.finaliseGame(sessionId, response);
        return ResponseEntity.ok(Map.of("gameSessionId", sessionId));
    }
}
