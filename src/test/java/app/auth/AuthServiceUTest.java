package app.auth;

import app.auth.service.AuthService;
import app.exception.DomainException;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void givenHappyFlow_registerSuccessfully() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("React123")
                .password("123123")
                .confirmPassword("123123")
                .build();

        when(userRepository.findByUsername("React123")).thenReturn(Optional.empty());

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("React123")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.register(registerRequest);
        verify(userRepository, times(1)).save(any(User.class));

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        int newRepositorySize = userRepository.findAll().size();

        assertEquals(1, newRepositorySize);
    }

    @Test
    void givenExistingUser_whenRegister_thenThrowException() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("React123")
                .password("123123")
                .confirmPassword("123123")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        assertThrows(DomainException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenLoginRequest_whenLoggingIsCalled_thenCreateRequest() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("React123")
                .email("react123@gmail.com")
                .firstName("Reat")
                .lastName("Tear")
                .role(UserRole.USER)
                .build();

        Map<String, Object> response = authService.createResponse(user);

        assertEquals(userId, response.get("userId"));
        assertEquals("React123", response.get("username"));
        assertEquals("react123@gmail.com", response.get("email"));
        assertEquals("Reat", response.get("firstName"));
        assertEquals("Tear", response.get("lastName"));
        assertEquals(UserRole.USER, response.get("role"));

    }

}
