package app.user;

import app.TestBuilder;
import app.exception.DomainException;
import app.security.AuthenticationMetadata;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void givenExistingUser_whenRegister_thenThrowException() {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("React123")
                .password("123123")
                .confirmPassword("123123")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        assertThrows(DomainException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("userStatusArguments")
    void whenUserStatusIsChanged_thenCorrectlyApplyNewStatus(boolean currentStatus, boolean expectedStatus) {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .isActive(currentStatus)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchStatus(userId);

        assertEquals(expectedStatus, user.isActive());
    }

    private static Stream<Arguments> userStatusArguments() {

        return Stream.of(
                Arguments.of(true, false),
                Arguments.of(false, true)
        );
    }

    @Test
    void whenFindAllUsersIsCalled_thenReturnAllUsers() {

        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void givenCorrectUsername_whenLoadUserByUsername_thenReturnUser() {

        String username = "Alibeb";
        User user = TestBuilder.newUser();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails authenticationMetaData = userService.loadUserByUsername(username);

        assertInstanceOf(AuthenticationMetadata.class, authenticationMetaData);
        AuthenticationMetadata result = (AuthenticationMetadata) authenticationMetaData;
        assertEquals(user.getId(), result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.isActive(), result.isActive());
    }

    @Test
    void givenMissingUsername_whenLoadUserByUsername_thenThrowException() {

        String username = "Alibeb";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void givenInactiveUsername_whenLoadUserByUsername_thenThrowException() {

        String username = "Alibeb";
        User user = User.builder()
                .isActive(false)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void givenExistingUsername_whenGetUserIdByUsername_thenReturnUserId() {

        String username = "Alibeb";

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UUID userId = userService.getUserIdByUsername(username);
        assertThat(user.getId()).isEqualTo(userId);
    }

    @Test
    void givenMissingUsername_whenGetUserIdByUsername_thenThrowException() {

        String username = "Alibeb";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserIdByUsername(username));
    }

}
