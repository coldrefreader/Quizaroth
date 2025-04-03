package app;

import app.auth.service.AuthService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class EditProfileITest {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;

    @Test
    public void editProfile_happyFlow() {

        RegisterRequest registerRequest = RegisterRequest
                .builder()
                .username("Ehehee")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User newRegisteredUser = authService.register(registerRequest);

        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("Kiye")
                .lastName("Ashas")
                .email("1@abv.bg")
                .build();

        UUID userId = newRegisteredUser.getId();
        userService.editUserInformation(userId, userEditRequest);

        User updatedUser = userService.getById(userId);
        assertEquals("Kiye", updatedUser.getFirstName());
        assertEquals("Ashas", updatedUser.getLastName());
        assertEquals("1@abv.bg", updatedUser.getEmail());
    }
}
