package app.user.service;

import app.exception.DomainException;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.UserEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    public void editUserInformation(UUID userId, UserEditRequest userEditRequest) {

        User user = getById(userId);

        user.setEmail(isBlank(userEditRequest.getEmail()) ? null : userEditRequest.getEmail());
        user.setFirstName(isBlank(userEditRequest.getFirstName()) ? null : userEditRequest.getFirstName());
        user.setLastName(isBlank(userEditRequest.getLastName()) ? null : userEditRequest.getLastName());

        userRepository.save(user);
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId).orElseThrow(() -> new DomainException("User with id [%s] not found".formatted(userId)));
    }

    @Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {

        User user = getById(userId);

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("User with[%s] info not found".formatted(username)));

        if (!user.isActive()) {
            throw new DomainException("Account is not active");
        }

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public UUID getUserIdByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with this info not found"));

        return user.getId();
    }
}
