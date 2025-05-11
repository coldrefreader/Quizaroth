package app.question.service;

import app.question.model.Question;
import app.question.repository.QuestionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CsvImporterService {

    private final QuestionRepository questionRepository;


    @Autowired
    public CsvImporterService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }



    @PostConstruct
    public void importQuestions()  {

        List<Question> newQuestions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("static/csv/questions.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Header is info about what the questions do, so it's skipped
                    continue;
                }

                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", - 1);
                if (tokens.length < 2) {
                    continue;
                }

                String text = tokens[1].replaceAll("\"", "");
                if (questionRepository.findByText(text).isPresent()) {
                    continue;
                }

                Question question = new Question();
                question.setText(tokens[1].replaceAll("\"", ""));
                question.setChoices(List.of(
                        tokens[2].replace("\"", ""),
                        tokens[3].replace("\"", ""),
                        tokens[4].replace("\"", "")));
                question.setCorrectAnswerIndex(Integer.parseInt(tokens[5]));

                if (tokens.length >= 7) {
                    question.setCategory(tokens[6].replaceAll("\"", "").trim());
                }

                newQuestions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!newQuestions.isEmpty()) {
            questionRepository.saveAll(newQuestions);
            log.info("Successfully imported {} question/s", newQuestions.size());
        } else {
            log.info("No new questions found");
        }
    }
}
