package app.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSessionRequest {

    //For creating sessions
    @NotNull(message = "Player 1 ID is required")
    private UUID player1Id;

    @NotNull(message = "Player 2 ID is required")
    private UUID player2Id;
}
