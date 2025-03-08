package app.question.service;

import app.question.model.Question;
import app.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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

        return questions.stream()
                .sorted((q1, q2) -> rand.nextInt(2) - 1)
                .limit(10)
                .toList();
    }

    public Optional<Question> getQuestionById(UUID id) {
        return questionRepository.findById(id);
    }
}
