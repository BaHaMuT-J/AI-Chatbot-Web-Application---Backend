package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.*;
import io.muzoo.ssc.project.backend.model.*;
import io.muzoo.ssc.project.backend.repository.*;
import io.muzoo.ssc.project.backend.service.CreateUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.util.Objects;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateUserService createUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/user/create")
    public UserDTO createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequest, BindingResult result) {
        if (result.hasErrors()) {
            return UserDTO.builder()
                    .success(false)
                    .message(Objects.requireNonNull(result.getFieldError()).getDefaultMessage())
                    .build();
        }

        String username = StringUtils.trim(createUserRequest.getUsername());
        String displayName = StringUtils.trim(createUserRequest.getDisplayName());
        String password = StringUtils.trim(createUserRequest.getPassword());

        User user = userRepository.findFirstByUsername(username);
        if (user != null) {
            return UserDTO
                    .builder()
                    .success(false)
                    .message(String.format("Username %s has already been taken.", username))
                    .build();
        } else {
            return createUserService.createUserAndReturnDTO(username, displayName, password);
        }
    }

    @PostMapping("/api/user/update")
    public SimpleResponseDTO updateDisplayName(@Valid @RequestBody UpdateDisplayNameRequestDTO updateDisplayNameRequestDTO, BindingResult result) {
        if (result.hasErrors()) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message(Objects.requireNonNull(result.getFieldError()).getDefaultMessage())
                    .build();
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());
            currentUser.setDisplayName(updateDisplayNameRequestDTO.getNewDisplayName());
            userRepository.save(currentUser);
            return SimpleResponseDTO.builder().success(true).message("Update display name successfully.").build();
        } else {
            // Should not come here, api is protected and only logged-in user can access
            return SimpleResponseDTO.builder().success(false).message("User must log in first.").build();
        }
    }

    @PostMapping("/api/user/changePassword")
    public SimpleResponseDTO changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, BindingResult result) {
        if (result.hasErrors()) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message(Objects.requireNonNull(result.getFieldError()).getDefaultMessage())
                    .build();
        }

        String currentPassword = StringUtils.trim(changePasswordRequestDTO.getCurrentPassword());
        String newPassword = StringUtils.trim(changePasswordRequestDTO.getNewPassword());
        String confirmPassword = StringUtils.trim(changePasswordRequestDTO.getConfirmNewPassword());

        if (!newPassword.equals(confirmPassword)) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("Passwords do not match.")
                    .build();
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());

            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                return SimpleResponseDTO.builder().success(false).message("Wrong current password.").build();
            }

            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);
            return SimpleResponseDTO.builder().success(true).message("Change password successfully.").build();
        } else {
            // Should not come here, api is protected and only logged-in user can access
            return SimpleResponseDTO.builder().success(false).message("User must log in first.").build();
        }
    }

    @GetMapping("/api/user/delete")
    public SimpleResponseDTO deleteUser(HttpServletRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());
            try {
                request.logout();
                userRepository.delete(currentUser);
                return SimpleResponseDTO
                        .builder()
                        .success(true)
                        .message("Your account has been deleted successfully.")
                        .build();
            } catch (ServletException e) {
                return SimpleResponseDTO
                        .builder()
                        .success(false)
                        .message(e.getMessage())
                        .build();
            }
        } else {
            // Should not come here, api is protected and only logged-in user can access
            return SimpleResponseDTO.builder().success(false).message("User must log in first.").build();
        }
    }
}
