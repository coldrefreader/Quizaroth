package app.user;

import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @ParameterizedTest
    @MethodSource("userRoleArguments")
    void whenUserRoleIsChanged_thenCorrectlyApplyNewRole(UserRole currentRole, UserRole expectedRole) {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(currentRole)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(expectedRole, user.getRole());
    }

    private static Stream<Arguments> userRoleArguments() {

        return Stream.of(
                Arguments.of(UserRole.USER, UserRole.ADMIN),
                Arguments.of(UserRole.ADMIN, UserRole.USER)
        );
    }


    //    public void register(@Valid RegisterRequest registerRequest) {
    //
    //        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());
    //
    //        if (optionalUser.isPresent()) {
    //            log.info("User already exists with username {}", registerRequest.getUsername());
    //            throw new DomainException("Username [%s] is already in use".formatted(registerRequest.getUsername()));
    //            //return false;
    //
    //        }
    //
    //        User user = userRepository.save(initialiseUser(registerRequest));
    //
    //        log.info("Successfully created new user with username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));
    //    }

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

        userService.register(registerRequest);
        verify(userRepository, times(1)).save(any(User.class));

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        int newRepositorySize = userRepository.findAll().size();

        assertEquals(1, newRepositorySize);
    }
}
