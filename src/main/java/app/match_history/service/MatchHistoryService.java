package app.match_history.service;

import app.game_sessions.model.GameSession;
import app.game_sessions.repository.GameSessionRepository;
import app.web.dto.MatchHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MatchHistoryService {

    private final GameSessionRepository gameSessionRepository;

    @Autowired
    public MatchHistoryService(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    public List<MatchHistoryRequest> getMatchHistory(UUID userId) {

        List<GameSession> gameSessions = gameSessionRepository.findTop10ByPlayer1IdOrPlayer2IdOrderByTimestampDesc(userId, userId);

        return gameSessions.stream().map(gameSession -> {

            String perspectiveResult;

            //Player1's result is same as gameSession's result
            //Player2's result is the opposite of gameSession's result
            boolean isPlayer1 = gameSession.getPlayer1().getId().equals(userId);
            perspectiveResult = isPlayer1 ? gameSession.getResult().name() :
                    switch (gameSession.getResult()) {
                        case VICTORY -> "DEFEAT";
                        case DEFEAT -> "VICTORY";
                        default -> "DRAW";
                    };


            String opponentUsername = isPlayer1 ? gameSession.getPlayer2().getUsername() : gameSession.getPlayer1().getUsername();

            int playerScore = isPlayer1 ? gameSession.getPlayer1Score() : gameSession.getPlayer2Score();
            int opponentScore = isPlayer1 ? gameSession.getPlayer2Score() : gameSession.getPlayer1Score();

            return new MatchHistoryRequest(
                    gameSession.getId(),
                    opponentUsername,
                    playerScore,
                    opponentScore,
                    perspectiveResult,
                    gameSession.getTimestamp()
            );
        }).toList();
    }
}
