package app.web;

import app.match_history.service.MatchHistoryService;
import app.web.dto.MatchHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/match-history")
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    @Autowired
    public MatchHistoryController(MatchHistoryService matchHistoryService) {
        this.matchHistoryService = matchHistoryService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<MatchHistoryRequest>> getMatchHistory(@PathVariable UUID userId) {
        return ResponseEntity.ok(matchHistoryService.getMatchHistory(userId));
    }
}
