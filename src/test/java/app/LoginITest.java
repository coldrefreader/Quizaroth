package app;


import app.auth.service.AuthService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.AuthController;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class LoginITest {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthController authController;


    @Test
    public void login_happyFlow() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUser")
                .password("testPassword")
                .confirmPassword("testPassword")
                .build();
        User newRegisteredUser = authService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testUser", "testPassword");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);
        when(mockRequest.getSession(true)).thenReturn(mockSession);

        ResponseEntity<?> responseEntity = authController.login(loginRequest, mockRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        assertEquals("Successful login", responseBody.get("message"));
        assertEquals(newRegisteredUser.getId().toString(), responseBody.get("userId"));
        assertEquals("testUser", responseBody.get("username"));
        assertEquals("USER", responseBody.get("role"));
    }
}
