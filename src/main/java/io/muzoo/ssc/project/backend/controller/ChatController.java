package io.muzoo.ssc.project.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.muzoo.ssc.project.backend.DTO.SendMessageRequestDTO;
import io.muzoo.ssc.project.backend.DTO.SendMessageResponseDTO;
import io.muzoo.ssc.project.backend.DTO.ChatDTO;
import io.muzoo.ssc.project.backend.DTO.ChatRequestDTO;
import io.muzoo.ssc.project.backend.model.AI;
import io.muzoo.ssc.project.backend.model.Chat;
import io.muzoo.ssc.project.backend.repository.ChatRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @PostMapping("/api/chat/getByUserAndAI")
    public ChatDTO getChat(@RequestBody ChatRequestDTO chatRequestDTO) {
        Chat chat = chatRepository.findFirstByUser_IdAndAi_Id(chatRequestDTO.getUserId(), chatRequestDTO.getAiId());
        return ChatDTO.builder().chatId(chat.getId()).build();
    }

    @PostMapping("/api/chat/send")
    public SendMessageResponseDTO sendMessage(@RequestBody SendMessageRequestDTO aiRequest, HttpServletRequest request) {
        Chat chat = chatRepository.findFirstById(aiRequest.getChatId());
        AI ai = chat.getAi();
        String aiAPI = ai.getApiLink();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = aiRequest.getPrompt();
        if (prompt == null || prompt.isEmpty()) {
            return SendMessageResponseDTO.builder().success(false).message("Missing prompt.").build();
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
                        return SendMessageResponseDTO.builder().response(part.get("text").asText()).build();
                    }
                }
            }

            return SendMessageResponseDTO.builder().success(false).message("Could not extract response.").build();

        } catch (Exception e) {
            return SendMessageResponseDTO.builder().success(false).message("An error occurred when processing the request.").build();
        }
    }
}
