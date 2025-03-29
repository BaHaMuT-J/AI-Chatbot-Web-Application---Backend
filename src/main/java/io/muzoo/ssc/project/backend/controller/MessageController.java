package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.ChatMessageResponseDTO;
import io.muzoo.ssc.project.backend.DTO.MessageDTO;
import io.muzoo.ssc.project.backend.DTO.MessageRequestDTO;
import io.muzoo.ssc.project.backend.DTO.SettingResponseDTO;
import io.muzoo.ssc.project.backend.model.Chat;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.ChatRepository;
import io.muzoo.ssc.project.backend.repository.MessageRepository;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MessageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/api/message/getByChat")
    public ChatMessageResponseDTO getMessages(@Valid @RequestBody MessageRequestDTO messageRequest, BindingResult result) {
        if (result.hasErrors()) {
            return ChatMessageResponseDTO.builder()
                    .success(false)
                    .message(Objects.requireNonNull(result.getFieldError()).getDefaultMessage())
                    .build();
        }

        Chat chat = chatRepository.findFirstById(messageRequest.getChatId());

        // Validate chat owner and current logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());
            if (!chat.getUser().getId().equals(currentUser.getId())) {
                return ChatMessageResponseDTO.builder()
                        .success(false)
                        .message("User cannot send prompt to chat of other users.")
                        .build();
            }
        } else {
            // Should not come here, api is protected and only logged-in user can access
            return ChatMessageResponseDTO.builder().success(false).message("User must log in first.").build();
        }

        return ChatMessageResponseDTO.builder().success(true).messagesList(messageRepository
                .findByChat_Id(messageRequest.getChatId())
                .stream()
                .map(msg -> MessageDTO.builder().isUser(msg.isUser()).text(msg.getText()).build())
                .toList()).build();
    }
}
