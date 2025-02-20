package app.question.service;

import app.question.model.Question;
import app.question.repository.QuestionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImporterService {

    private final QuestionRepository questionRepository;


    @Autowired
    public CsvImporterService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @PostConstruct
    public void importQuestions(){
        if (questionRepository.count() > 0) {
            System.out.println("Questions are already imported, skipping...");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("static/csv/questions.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            List<Question> questions = new ArrayList<>();

            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                if (firstLine) {
                    firstLine = false; // Header is info about what the questions do, so it's skipped
                    continue;
                }

                System.out.println(line);

                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", - 1);
                if (tokens.length < 2) {
                    continue;
                }

                Question question = new Question();
                question.setText(tokens[1].replaceAll("\"", ""));
                question.setChoices(List.of(
                        tokens[2].replace("\"", ""),
                        tokens[3].replace("\"", ""),
                        tokens[4].replace("\"", "")));
                question.setCorrectAnswerIndex(Integer.parseInt(tokens[5]));

                questions.add(question);
            }

            questionRepository.saveAll(questions);
            System.out.println("Successfully imported " + questions.size() + " questions");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
