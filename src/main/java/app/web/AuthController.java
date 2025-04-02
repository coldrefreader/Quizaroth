package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        userService.register(registerRequest);
        return ResponseEntity.ok(Map.of("message", "User successfully registered!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        log.info("Login request: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        if (session != null) {
            log.info("Session created with ID: {}", session.getId());
        } else {
            log.warn("No session created");
        }

        AuthenticationMetadata authUser = (AuthenticationMetadata) authentication.getPrincipal();

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

        Map<String, Object> response = new HashMap<>();
        response.put("userId", updatedUser.getId());
        response.put("username", updatedUser.getUsername());
        response.put("email", updatedUser.getEmail());
        response.put("firstName", updatedUser.getFirstName());
        response.put("lastName", updatedUser.getLastName());
        response.put("role", updatedUser.getRole());

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
