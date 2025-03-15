package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.CreateUserRequestDTO;
import io.muzoo.ssc.project.backend.DTO.UserDTO;
import io.muzoo.ssc.project.backend.model.AI;
import io.muzoo.ssc.project.backend.model.Chat;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.AIRepository;
import io.muzoo.ssc.project.backend.repository.ChatRepository;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIRepository aiRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/user/create")
    public UserDTO createUser(@RequestBody CreateUserRequestDTO createUserRequest, HttpServletRequest request) {
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

            List<AI> ais = aiRepository.findAll();
            for (AI ai : ais) {
                Chat chat = new Chat();
                chat.setUser(user);
                chat.setAi(ai);
                chatRepository.save(chat);
            }

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
