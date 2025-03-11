package app.answer.repository;


import app.answer.model.Answer;
import app.game_sessions.model.GameSession;
import app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {

//    List<Answer> findByGameSessionId(UUID gameSessionId);

    int countByGameSessionAndPlayer(GameSession gameSession, User player);
}
