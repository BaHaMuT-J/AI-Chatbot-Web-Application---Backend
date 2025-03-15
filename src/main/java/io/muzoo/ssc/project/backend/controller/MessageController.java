package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.MessageDTO;
import io.muzoo.ssc.project.backend.DTO.MessageRequestDTO;
import io.muzoo.ssc.project.backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/api/message/getByChat")
    public List<MessageDTO> getMessages(@RequestBody MessageRequestDTO messageRequestDTO) {
        return messageRepository
                .findByChat_Id(messageRequestDTO.getChatId())
                .stream()
                .map(msg -> MessageDTO.builder().isUser(msg.isUser()).text(msg.getText()).build())
                .toList();
    }
}
