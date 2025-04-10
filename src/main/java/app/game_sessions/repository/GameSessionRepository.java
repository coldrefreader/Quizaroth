package app.game_sessions.repository;

import app.game_sessions.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    List<GameSession> findTop10ByPlayer1IdOrPlayer2IdOrderByTimestampDesc(UUID player1Id, UUID player2Id);
}
