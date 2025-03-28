package io.muzoo.ssc.project.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.muzoo.ssc.project.backend.DTO.*;
import io.muzoo.ssc.project.backend.model.AI;
import io.muzoo.ssc.project.backend.model.Chat;
import io.muzoo.ssc.project.backend.model.Message;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.*;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ModelCurrentRepository modelCurrentRepository;

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private MaxTokenRepository maxTokenRepository;

    @PostMapping("/api/chat/getByUserAndAI")
    public ChatDTO getChat(@RequestBody ChatRequestDTO chatRequestDTO) {
        Chat chat = chatRepository.findFirstByUser_IdAndAi_Id(chatRequestDTO.getUserId(), chatRequestDTO.getAiId());
        return ChatDTO.builder().chatId(chat.getId()).build();
    }

    @PostMapping("/api/chat/send")
    public SendMessageResponseDTO sendMessage(@RequestBody SendMessageRequestDTO sendMessageRequest) {
        Chat chat = chatRepository.findFirstById(sendMessageRequest.getChatId());

        // Validate chat owner and current logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());
            if (!chat.getUser().getId().equals(currentUser.getId())) {
                return SendMessageResponseDTO.builder()
                        .success(false)
                        .message("User cannot send prompt to chat of other users.")
                        .build();
            }
        } else {
            return SendMessageResponseDTO.builder().success(false).message("User must log in first.").build();
        }

        AI ai = chat.getAi();
        String prompt = sendMessageRequest.getPrompt();

        // Validate prompt not blank
        if (prompt == null || prompt.isBlank()) {
            return SendMessageResponseDTO.builder().success(false).message("Missing prompt.").build();
        }

        String aiName = ai.getName();

        return switch (aiName) {
            case "Gemini" -> getGeminiResponse(chat, ai.getApiLink(), sendMessageRequest.getPrompt());
            case "groq" -> getGroqResponse(chat, ai, sendMessageRequest.getPrompt());
            case "DeepSeek" -> getDeepSeekResponse(chat, ai, sendMessageRequest.getPrompt());
            case "Mistral" -> getMistralResponse(chat, ai, sendMessageRequest.getPrompt());
            default -> SendMessageResponseDTO.builder()
                    .success(false)
                    .message(String.format("No AI with name %s.", aiName))
                    .build();
        };
    }

    private void saveChat(Chat chat, String prompt, String responseText) {
        Message userMsg = new Message();
        userMsg.setChat(chat);
        userMsg.setUser(true);
        userMsg.setText(prompt);
        messageRepository.save(userMsg);

        Message aiMsg = new Message();
        aiMsg.setChat(chat);
        aiMsg.setUser(false);
        aiMsg.setText(responseText);
        messageRepository.save(aiMsg);
    }

    private SendMessageResponseDTO getGeminiResponse(Chat chat, String aiAPI, String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

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

            if (rootNode.has("candidates") && rootNode.get("candidates").isArray() && !rootNode.get("candidates").isEmpty()) {
                JsonNode candidate = rootNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts") && candidate.get("content").get("parts").isArray() && !candidate.get("content").get("parts").isEmpty()) {
                    JsonNode part = candidate.get("content").get("parts").get(0);
                    if (part.has("text")) {
                        String responseText = part.get("text").asText();
                        saveChat(chat, prompt, responseText);
                        return SendMessageResponseDTO.builder().success(true).response(responseText).build();
                    }
                }
            }

            return SendMessageResponseDTO.builder()
                    .success(false)
                    .message("Could not extract response.")
                    .build();

        } catch (Exception e) {
            return SendMessageResponseDTO.builder()
                    .success(false)
                    .message("An error occurred when processing the request.")
                    .build();
        }
    }

    private SendMessageResponseDTO getGroqResponse(Chat chat, AI ai, String prompt) {
        Dotenv dotenv = Dotenv.load();
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(ai.getApiLink())
                .apiKey(dotenv.get("GROQ_API_KEY"))
                .build();
        Long userId = chat.getUser().getId(), aiId = ai.getId();
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(modelCurrentRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getModelName())
                .temperature(temperatureRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getTemperature())
                .maxTokens(maxTokenRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getMaxToken())
                .build();
        String responseText = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(openAiChatOptions)
                .build()
                .call(prompt);
        saveChat(chat, prompt, responseText);
        return SendMessageResponseDTO.builder().success(true).response(responseText).build();
    }

    private SendMessageResponseDTO getDeepSeekResponse(Chat chat, AI ai, String prompt) {
        Dotenv dotenv = Dotenv.load();
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(ai.getApiLink())
                .apiKey(dotenv.get("DEEPSEEK_API_KEY"))
                .build();
        Long userId = chat.getUser().getId(), aiId = ai.getId();
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(modelCurrentRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getModelName())
                .temperature(temperatureRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getTemperature())
                .maxTokens(maxTokenRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getMaxToken())
                .build();
        String responseText = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(openAiChatOptions)
                .build()
                .call(prompt);
        saveChat(chat, prompt, responseText);
        return SendMessageResponseDTO.builder().success(true).response(responseText).build();
    }

    private SendMessageResponseDTO getMistralResponse(Chat chat, AI ai, String prompt) {
        Dotenv dotenv = Dotenv.load();
        MistralAiApi mistralAiApi = new MistralAiApi(dotenv.get("MISTRAL_API_KEY"));
        Long userId = chat.getUser().getId(), aiId = ai.getId();
        MistralAiChatOptions mistralAiChatOptions = MistralAiChatOptions.builder()
                .model(modelCurrentRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getModelName())
                .temperature(temperatureRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getTemperature())
                .maxTokens(maxTokenRepository.findFirstByUser_IdAndAi_Id(userId, aiId).getMaxToken())
                .build();
        String responseText = MistralAiChatModel.builder()
                .mistralAiApi(mistralAiApi)
                .defaultOptions(mistralAiChatOptions)
                .build()
                .call(prompt);
        saveChat(chat, prompt, responseText);
        return SendMessageResponseDTO.builder().success(true).response(responseText).build();
    }
}
