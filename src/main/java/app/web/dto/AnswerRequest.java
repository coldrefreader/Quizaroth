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
public class AnswerRequest {

    @NotNull
    private UUID gameSessionId;

    @NotNull
    private UUID playerId;

    @NotNull
    private UUID questionId;

    @NotNull
    private Integer selectedAnswerIndex;
}
