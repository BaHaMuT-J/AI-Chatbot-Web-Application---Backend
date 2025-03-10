package io.muzoo.ssc.project.backend.auth;

import io.muzoo.ssc.project.backend.DTO.CreateUserRequestDTO;
import io.muzoo.ssc.project.backend.DTO.LoginRequestDTO;
import io.muzoo.ssc.project.backend.DTO.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.DTO.UserDTO;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/login")
    public UserDTO login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        String username = StringUtils.trim(loginRequest.getUsername());
        String password = StringUtils.trim(loginRequest.getPassword());
        try {
            // if there is a current user logged in, if so log that user out first
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }
            request.login(username, password);

            User user = userRepository.findFirstByUsername(username);
            return UserDTO
                    .builder()
                    .success(true)
                    .message("You are successfully logged in.")
                    .username(user.getUsername())
                    .displayName(user.getDisplayName())
                    .build();
        } catch (ServletException e) {
            return UserDTO
                    .builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("/api/logout")
    public SimpleResponseDTO logout(HttpServletRequest request) {
        try {
            request.logout();
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("You are successfully logged out.")
                    .build();
        } catch (ServletException e) {
            return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("/api/user/create")
    public UserDTO create(@RequestBody CreateUserRequestDTO createUserRequest, HttpServletRequest request) {
        String username = StringUtils.trim(createUserRequest.getUsername());
        String displayName = StringUtils.trim(createUserRequest.getDisplayName());
        String password = StringUtils.trim(createUserRequest.getPassword());

        if (username == null || username.isEmpty() || password == null || password.isEmpty() || displayName == null || displayName.isEmpty()) {
            return UserDTO
                    .builder()
                    .success(false)
                    .message("Missing information.")
                    .build();
        }

        User user = userRepository.findFirstByUsername(username);
        if (user != null) {
            return UserDTO
                    .builder()
                    .success(false)
                    .message(String.format("Username %s has already been taken.", username))
                    .build();
        } else {
            user = new User();
            user.setUsername(username);
            user.setDisplayName(displayName);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return UserDTO
                    .builder()
                    .success(true)
                    .message("You are successfully registered. Please login.")
                    .username(user.getUsername())
                    .displayName(user.getDisplayName())
                    .build();
        }
    }

}
