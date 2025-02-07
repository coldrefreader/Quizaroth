package app.leaderboard.repository;


import app.leaderboard.model.Leaderboard;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID> {

    Optional<Leaderboard> findByUser(User user);
    List<Leaderboard> findTop10ByOrderByWinRateDesc();
}
