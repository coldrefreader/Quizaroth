package app;

import app.user.model.User;
import app.user.model.UserRole;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static User newUser() {

        return User.builder()
                .username(UUID.randomUUID().toString())
                .password("123123")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .lastUpdatedOn(LocalDateTime.now())
                .build();
    }

    public static User newUser2() {

        return User.builder()
                .username(UUID.randomUUID().toString())
                .password("123123")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .lastUpdatedOn(LocalDateTime.now())
                .build();
    }
}
