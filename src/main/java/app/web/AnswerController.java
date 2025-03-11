package app.web;


import app.answer.model.Answer;
import app.answer.service.AnswerService;
import app.web.dto.AnswerRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/answers")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitAnswer(@Valid @RequestBody AnswerRequest request) {

        Answer savedAnswer = answerService.submitAnswer(request);

        return ResponseEntity.ok().body("Answer submitted successfully: " + (savedAnswer.isCorrect() ? "Correct" : "Incorrect"));
    }
}
