package app.web;

import app.auth.service.AuthService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, AuthService authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        authService.register(registerRequest);
        return ResponseEntity.ok(Map.of("message", "User successfully registered!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        AuthenticationMetadata authUser = authService.login(loginRequest, request);

        return ResponseEntity.ok(Map.of(
                "message", "Successful login",
                "userId", authUser.getUserId().toString(),
                "username", authUser.getUsername(),
                "role", authUser.getRole().name()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@AuthenticationPrincipal AuthenticationMetadata userDetails) {

        log.info("Checking authentication for /me");

        User updatedUser = userService.getById(userDetails.getUserId());
        log.info("Authenticated user: {}", userDetails.getUsername());

        Map<String, Object> response = authService.createResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editUserProfile(@Valid @RequestBody UserEditRequest userEditRequest, Principal principal) {

        UUID userId = userService.getUserIdByUsername(principal.getName());
        log.info("Editing profile for user: {}", userId);

        userService.editUserInformation(userId, userEditRequest);
        return ResponseEntity.ok(Map.of("message", "Profile successfully updated"));
    }
}
