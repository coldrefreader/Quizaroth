package app.question.service;

import app.question.model.Question;
import app.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final Random rand = new Random();

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getRandomQuestions() {
        List<Question> questions = questionRepository.findAll();

        if (questions.size() <= 10) {
            return questions;
        }

        Collections.shuffle(questions);
        return questions.stream()
                .limit(10)
                .toList();
    }

    public Optional<Question> getQuestionById(UUID id) {
        return questionRepository.findById(id);
    }
}
