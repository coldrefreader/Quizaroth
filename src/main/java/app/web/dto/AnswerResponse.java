package app.web.dto;

import java.util.UUID;

public record AnswerResponse(

        UUID id,
        String questionText,
        String playerUsername,
        int selectedAnswerIndex,
        boolean isCorrect
) {}
