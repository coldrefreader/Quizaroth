package app;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuizarothApplication {



    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();


        System.out.println("Loaded DB URL: " + dotenv.get("DB_URL"));

        SpringApplication.run(QuizarothApplication.class, args);
    }

}
