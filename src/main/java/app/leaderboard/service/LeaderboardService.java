package app.leaderboard.service;

import app.leaderboard.client.LeaderboardClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class LeaderboardService {

    private final LeaderboardClient leaderboardClient;

    @Autowired
    public LeaderboardService(LeaderboardClient leaderboardClient) {
        this.leaderboardClient = leaderboardClient;
    }

    //Sends results to the Leaderboard microservice
    public void updateLeaderboard(String username, boolean isWinner) {

        log.info("Sending request to update leaderboard for username {}", username);
        leaderboardClient.updateLeaderboard(username, isWinner);
        log.info("Successfully updated leaderboard for user {}", username);
    }
}
