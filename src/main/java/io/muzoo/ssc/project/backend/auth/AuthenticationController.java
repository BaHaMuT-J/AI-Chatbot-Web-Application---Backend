package io.muzoo.ssc.project.backend.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.muzoo.ssc.project.backend.DTO.*;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.AIRepository;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIRepository aiRepository;

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

    @PostMapping("/api/ai/response")
    public Map<String, Object> generateContent(@RequestBody AIRequestDTO aiRequest, HttpServletRequest request) {
        String aiAPI = aiRepository.findFirstByName(aiRequest.getAi()).getApiLink();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = aiRequest.getPrompt();
        if (prompt == null || prompt.isEmpty()) {
            return Map.of("error", "Prompt is required.");
        }

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", prompt);
        contents.put("parts", java.util.Collections.singletonList(parts));
        requestBody.put("contents", java.util.Collections.singletonList(contents));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(aiAPI, HttpMethod.POST, entity, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            if (rootNode.has("candidates") && rootNode.get("candidates").isArray() && rootNode.get("candidates").size() > 0) {
                JsonNode candidate = rootNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts") && candidate.get("content").get("parts").isArray() && candidate.get("content").get("parts").size() > 0) {
                    JsonNode part = candidate.get("content").get("parts").get(0);
                    if (part.has("text")) {
                        // TODO : store response in database
                        return Map.of("response", part.get("text").asText());
                    }
                }
            }

            return Map.of("error", "Could not extract response."); // Handle case where response is not found.

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "An error occurred processing the request.");
        }
    }

}
