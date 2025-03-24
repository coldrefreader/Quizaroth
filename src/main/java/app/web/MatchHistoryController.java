package app.web;

import app.match_history.service.MatchHistoryService;
import app.security.AuthenticationMetadata;
import app.web.dto.MatchHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/match-history")
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    @Autowired
    public MatchHistoryController(MatchHistoryService matchHistoryService) {
        this.matchHistoryService = matchHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<MatchHistoryRequest>> getMatchHistory(
            @AuthenticationPrincipal AuthenticationMetadata userDetails) {

        return ResponseEntity.ok(matchHistoryService.getMatchHistory(userDetails.getUserId()));
    }
}
