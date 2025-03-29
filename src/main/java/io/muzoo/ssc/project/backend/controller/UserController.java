package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.CreateUserRequestDTO;
import io.muzoo.ssc.project.backend.DTO.UserDTO;
import io.muzoo.ssc.project.backend.model.*;
import io.muzoo.ssc.project.backend.repository.*;
import io.muzoo.ssc.project.backend.service.CreateUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateUserService createUserService;

    @PostMapping("/api/user/create")
    public UserDTO createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequest) {
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
}
