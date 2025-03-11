package app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSessionResponse {

    //For the actual matches

    private UUID id;
    private String player1;
    private String player2;
    private int player1Score;
    private int player2Score;
    private String result;
    private List<AnswerResponse> answers;
}
