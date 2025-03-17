package app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchHistoryRequest {

    private UUID gameSessionId;
    private String opponentUsername;
    private int playerScore;
    private int opponentScore;
    private String result;
    private LocalDateTime timestamp;
}
