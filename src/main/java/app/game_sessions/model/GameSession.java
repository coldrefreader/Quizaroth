package app.game_sessions.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private User player2;

    @Column(nullable = false)
    private int player1Score;

    @Column(nullable = false)
    private int player2Score;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameResult result;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
