package app.question.service;

import app.question.model.Question;
import app.question.repository.QuestionRepository;
import app.web.dto.QuestionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionService {

    private static final int PICK_COUNT = 10; // There is a query deciding it as well, this is a second line of defence and they should be synced
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionRequest> getRandomQuestions(String category) {

        List<Question> questions = questionRepository.findRandomByCategory(category);

        List<Question> cappedQuestions = questions.size() <= PICK_COUNT
                ? questions
                : questions.subList(0, PICK_COUNT);

        return cappedQuestions.stream()
                .map(q -> new QuestionRequest(
                        q.getId(),
                        q.getText(),
                        q.getChoices(),
                        q.getCorrectAnswerIndex(),
                        q.getCategory()
                ))
                .toList();
    }
}
