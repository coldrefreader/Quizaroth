package app.web;

import app.security.AuthenticationMetadata;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

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

        if (userDetails == null) {
            log.warn("No authentication found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not authenticated"));
        }

        log.info("Authenticated user: {}", userDetails.getUsername());

        return ResponseEntity.ok(Map.of(
                "userId", userDetails.getUserId(),
                "username", userDetails.getUsername(),
                "role", userDetails.getRole().name()));
    }
}
