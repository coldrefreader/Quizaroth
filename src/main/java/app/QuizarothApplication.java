package app;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication
@EnableFeignClients
public class QuizarothApplication {



    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();


        log.info("Loaded DB URL: {}", dotenv.get("DB_URL"));

        SpringApplication.run(QuizarothApplication.class, args);
    }

}
