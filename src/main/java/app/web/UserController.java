package app.web;

import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{userId}/status")
    public void switchStatus(@PathVariable UUID userId) {
        userService.switchStatus(userId);
    }

    @PutMapping("/users/{userId}/role")
    public void switchRole(@PathVariable UUID userId) {
        userService.switchRole(userId);
    }

}
