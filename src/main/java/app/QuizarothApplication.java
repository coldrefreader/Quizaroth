package app;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class QuizarothApplication {



    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();


        log.info("Loaded DB URL: {}", dotenv.get("DB_URL"));

        SpringApplication.run(QuizarothApplication.class, args);
    }

}
