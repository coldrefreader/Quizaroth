package app.game_sessions.service;

import app.answer.repository.AnswerRepository;
import app.exception.DomainException;
import app.game_sessions.model.GameResult;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.leaderboard.service.LeaderboardService;
import app.user.model.User;
import app.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final LeaderboardService leaderboardService;

    @Autowired
    public GameSessionService(GameSessionRepository gameSessionRepository, UserRepository userRepository, AnswerRepository answerRepository, LeaderboardService leaderboardService) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    public GameSession createGameSession(UUID player1Id, UUID player2Id) {

        User player1 = userRepository.findById(player1Id)
                .orElseThrow(() -> new DomainException("User with id " + player1Id + " not found"));

        User player2 = userRepository.findById(player2Id)
                .orElseThrow(() -> new DomainException("User with id " + player2Id + " not found"));

        GameSession gameSession = GameSession.builder()
                .player1(player1)
                .player2(player2)
                .player1Score(0)
                .player2Score(0)
                .result(GameResult.UNDETERMINED)
                .timestamp(LocalDateTime.now())
                .build();

        return gameSessionRepository.save(gameSession);
    }


    @Transactional
    public void trackAndCompleteGame(UUID gameSessionId) {

        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new DomainException("Game session with id " + gameSessionId + " not found"));

        int player1Answers = answerRepository.countByGameSessionAndPlayer(gameSession, gameSession.getPlayer1());
        int player2Answers = answerRepository.countByGameSessionAndPlayer(gameSession, gameSession.getPlayer2());

        if (player1Answers == 10 && player2Answers == 10) {
            finaliseGame(gameSession);
        }
    }


    @Transactional
    public void finaliseGame(GameSession gameSession) {

        if (gameSession.getResult() != GameResult.UNDETERMINED) {
            log.warn("Game session {} already finalised with result: {}", gameSession.getId(), gameSession.getResult());
            return;
        }

        int player1Score = gameSession.getPlayer1Score();
        int player2Score = gameSession.getPlayer2Score();
        String player1Username = gameSession.getPlayer1().getUsername();
        String player2Username = gameSession.getPlayer2().getUsername();

        calculateScoreAndUpdateLeaderboard(gameSession, player1Score, player2Score, player1Username, player2Username);

        gameSessionRepository.save(gameSession);
        log.info("Game session has been finalised : {}", gameSession.getResult());
    }


    public GameSession getGameSessionById(UUID gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new DomainException("Game session with id " + gameSessionId + " not found"));
    }


    private void calculateScoreAndUpdateLeaderboard(GameSession gameSession, int player1Score, int player2Score, String player1Username, String player2Username) {
        if (player1Score > player2Score) {

            log.info("Victory for Player 1! {}'s score - {}; {}'s score - {}.",
                    player1Username, player1Score, player2Username, player2Score);
            leaderboardService.updateLeaderboard(player1Username, true);
            leaderboardService.updateLeaderboard(player2Username, false);
            gameSession.setResult(GameResult.VICTORY);
        } else if (player1Score < player2Score) {

            log.info("Victory for Player 2! {}'s score - {}; {}'s score - {}.",
                    player2Username, player2Score, player1Username, player1Score);
            leaderboardService.updateLeaderboard(player2Username, true);
            leaderboardService.updateLeaderboard(player1Username, false);
            gameSession.setResult(GameResult.DEFEAT);
        } else {

            log.info("It's a draw! {}'s score - {}; {}'s score - {}.",
                    gameSession.getPlayer1().getUsername(), player1Score, gameSession.getPlayer2().getUsername(), player2Score);
            leaderboardService.updateLeaderboard(player1Username, false);
            leaderboardService.updateLeaderboard(player2Username, false);
            gameSession.setResult(GameResult.DRAW);
        }
    }
}
