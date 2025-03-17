package app.leaderboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "leaderboard-service", url = "http://localhost:8081/v1/leaderboard")
public interface LeaderboardClient {

    @PostMapping("/update")
    void updateLeaderboard(@RequestParam String username, @RequestParam boolean isWinner);
}
