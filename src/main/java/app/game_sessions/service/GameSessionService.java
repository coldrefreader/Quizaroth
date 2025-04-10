package app.game_sessions.service;

import app.exception.DomainException;
import app.game_sessions.model.GameResult;
import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.leaderboard.service.LeaderboardService;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.web.dto.FinaliseGameStateResponse;
import app.web.dto.GameSessionResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final LeaderboardService leaderboardService;

    @Autowired
    public GameSessionService(GameSessionRepository gameSessionRepository, UserRepository userRepository, LeaderboardService leaderboardService) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
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
    public void finaliseGame(UUID sessionId, FinaliseGameStateResponse finalState) {

        GameSession gameSession = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new DomainException("Game session with id " + sessionId + " not found"));

        if (gameSession.getResult() != GameResult.UNDETERMINED) {
            log.warn("Game session {} already finalised with result: {}", gameSession.getId(), gameSession.getResult());
            return;
        }

        Map<String, Integer> finalScores = finalState.getPlayerScores();

        int finalPlayer1Score = finalScores.getOrDefault(gameSession.getPlayer1().getUsername(), gameSession.getPlayer1Score());
        int finalPlayer2Score = finalScores.getOrDefault(gameSession.getPlayer2().getUsername(), gameSession.getPlayer2Score());

        gameSession.setPlayer1Score(finalPlayer1Score);
        gameSession.setPlayer2Score(finalPlayer2Score);

        calculateScore(gameSession, finalPlayer1Score, finalPlayer2Score,
                gameSession.getPlayer1().getUsername(), gameSession.getPlayer2().getUsername());

        gameSessionRepository.save(gameSession);
        log.info("Game session has been finalised : {}", gameSession.getResult());

        updateLeaderboardAsync(finalPlayer1Score, finalPlayer2Score,
                gameSession.getPlayer1().getUsername(), gameSession.getPlayer2().getUsername());
    }

    private void updateLeaderboardAsync(int finalPlayer1Score, int finalPlayer2Score, String player1, String player2) {

        try {
            updateLeaderboard(finalPlayer1Score, finalPlayer2Score, player1, player2);
        } catch (Exception e) {
            log.error("Leaderboard update failed for players {} and {}: {}", player1, player2, e.getMessage());
        }
    }

    public GameSession getGameSessionById(UUID gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new DomainException("Game session with id " + gameSessionId + " not found"));
    }


    private void calculateScore(GameSession gameSession, int player1Score, int player2Score, String player1Username, String player2Username) {

        if (player1Score > player2Score) {
            log.info("Victory for Player 1! {}'s score - {}; {}'s score - {}.",
                    player1Username, player1Score, player2Username, player2Score);
            gameSession.setResult(GameResult.VICTORY);
        } else if (player1Score < player2Score) {
            log.info("Victory for Player 2! {}'s score - {}; {}'s score - {}.",
                    player2Username, player2Score, player1Username, player1Score);
            gameSession.setResult(GameResult.DEFEAT);
        } else {
            log.info("It's a draw! {}'s score - {}; {}'s score - {}.",
                    gameSession.getPlayer1().getUsername(), player1Score, gameSession.getPlayer2().getUsername(), player2Score);
            gameSession.setResult(GameResult.DRAW);
        }
    }

    private void updateLeaderboard(int player1Score, int player2Score, String player1Username, String player2Username) {

        if (player1Score > player2Score) {
            leaderboardService.updateLeaderboard(player1Username, true);
            leaderboardService.updateLeaderboard(player2Username, false);
        } else if (player1Score < player2Score) {
            leaderboardService.updateLeaderboard(player2Username, true);
            leaderboardService.updateLeaderboard(player1Username, false);
        } else {
            leaderboardService.updateLeaderboard(player1Username, false);
            leaderboardService.updateLeaderboard(player2Username, false);
        }

    }



    public GameSessionResponse createRequest(GameSession gameSession) {

        return new GameSessionResponse(
                gameSession.getId(),
                gameSession.getPlayer1().getUsername(),
                gameSession.getPlayer2().getUsername(),
                gameSession.getPlayer1Score(),
                gameSession.getPlayer2Score(),
                gameSession.getResult().name()
        );
    }
}
