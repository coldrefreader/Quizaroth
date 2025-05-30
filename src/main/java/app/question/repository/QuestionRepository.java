package app.question.repository;

import app.question.model.Question;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    Optional<Question> findByText(String text);

    @Query(
            value = "SELECT * FROM question WHERE category =:category ORDER BY RAND() LIMIT 10",
            nativeQuery = true
    )
    List<Question> findRandomByCategory(
            @Param("category") String category
    );
}
