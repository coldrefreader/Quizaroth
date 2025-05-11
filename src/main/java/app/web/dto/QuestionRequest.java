package app.web.dto;


import java.util.List;
import java.util.UUID;


public record QuestionRequest (

    UUID id,
    String text,
    List<String> choices,
    int correctAnswerIndex,
    String category
    ) {}
