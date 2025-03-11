package app.answer.service;

import app.answer.model.Answer;
import app.answer.repository.AnswerRepository;
import app.exception.DomainException;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.game_sessions.service.GameSessionService;
import app.question.model.Question;
import app.question.repository.QuestionRepository;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.web.dto.AnswerRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final GameSessionService gameSessionService;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, GameSessionRepository gameSessionRepository, UserRepository userRepository, QuestionRepository questionRepository, GameSessionService gameSessionService) {
        this.answerRepository = answerRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.gameSessionService = gameSessionService;
    }

    @Transactional
    public Answer submitAnswer(AnswerRequest request) {

        GameSession gameSession = gameSessionRepository.findById(request.getGameSessionId())
                .orElseThrow(() -> new RuntimeException("Game session not found"));

        User player = userRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        int playerAnswerCount = answerRepository.countByGameSessionAndPlayer(gameSession, player);

        if (playerAnswerCount >= 10) {
            throw new DomainException("%s already has 10 questions answered this session".formatted(player.getUsername()));
        }

        boolean isCorrect = request.getSelectedAnswerIndex().equals(question.getCorrectAnswerIndex());

        Answer answer = Answer.builder()
                .gameSession(gameSession)
                .player(player)
                .question(question)
                .selectedAnswerIndex(request.getSelectedAnswerIndex())
                .isCorrect(isCorrect)
                .build();

        if (isCorrect) {
            if (gameSession.getPlayer1().equals(player)) {
                gameSession.setPlayer1Score(gameSession.getPlayer1Score() + 1);
            } else if (gameSession.getPlayer2().equals(player)) {
                gameSession.setPlayer2Score(gameSession.getPlayer2Score() + 1);
            }
            gameSessionRepository.save(gameSession);
        }

        Answer result = answerRepository.save(answer);
        if (playerAnswerCount == 9) {
            gameSessionService.trackAndCompleteGame(request.getGameSessionId());
        }

        if (log.isTraceEnabled()) {
            log.trace(result.toString());
        }
        return result;
    }
}
