package io.muzoo.ssc.project.backend.auth;

import io.muzoo.ssc.project.backend.DTO.LoginRequestDTO;
import io.muzoo.ssc.project.backend.DTO.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.DTO.UserDTO;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/login")
    public UserDTO login(@RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
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

}
