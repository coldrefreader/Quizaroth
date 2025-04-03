package app.question;

import app.question.model.Question;
import app.question.repository.QuestionRepository;
import app.question.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceUTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void givenLessThanTenQuestions_whenGetQuestions_thenReturnQuestions() {

        List<Question> questions = Arrays.asList(
                Question.builder().id(UUID.randomUUID()).text("Question 1").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(0).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 2").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(1).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 3").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(2).build()
        );

        when(questionRepository.findAll()).thenReturn(questions);

        List<Question> result = questionService.getRandomQuestions();

        assertEquals(questions.size(), result.size());
        assertEquals(questions, result);
    }

    @Test
    void givenMoreThanTenQuestions_whenGetQuestions_thenReturnQuestions() {

        List<Question> questions = Arrays.asList(
                Question.builder().id(UUID.randomUUID()).text("Question 4").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(0).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 5").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(1).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 6").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(2).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 7").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(0).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 8").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(1).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 9").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(2).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 10").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(0).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 11").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(1).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 12").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(2).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 13").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(2).build(),
                Question.builder().id(UUID.randomUUID()).text("Question 14").choices(Arrays.asList("Choice 1", "Choice 2", "Choice 3"))
                        .correctAnswerIndex(2).build()
        );

        when(questionRepository.findAll()).thenReturn(questions);

        List<Question> result = questionService.getRandomQuestions();

        assertEquals(10, result.size());
    }


}
