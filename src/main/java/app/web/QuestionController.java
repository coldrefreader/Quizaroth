package app.web;

import app.question.service.QuestionService;
import app.web.dto.QuestionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionRequest>> getQuestions() {

        List<QuestionRequest> request = questionService.getRandomQuestions()
                .stream()
                .map((question -> new QuestionRequest(
                        question.getId(),
                        question.getText(),
                        question.getChoices(),
                        question.getCorrectAnswerIndex()
                ))).toList();

        return ResponseEntity.ok(request);
    }
}
