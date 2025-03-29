package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.*;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.util.Objects;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/login")
    public UserDTO login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return UserDTO.builder()
                    .success(false)
                    .message(Objects.requireNonNull(result.getFieldError()).getDefaultMessage())
                    .build();
        }

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

}
