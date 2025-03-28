package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.AIDTO;
import io.muzoo.ssc.project.backend.model.ModelAvailable;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AIController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIRepository aiRepository;

    @Autowired
    private ModelCurrentRepository modelCurrentRepository;

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private MaxTokenRepository maxTokenRepository;

    @Autowired
    private ModelAvailableRepository modelAvailableRepository;

    @GetMapping("/api/ai/models")
    public List<AIDTO> getModels() {
        // Get UserId from currently logged-in user
        long userId;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());
            userId = currentUser.getId();
        } else {
            userId = 0;
        }

        return aiRepository
                .findAll()
                .stream()
                .map(ai -> AIDTO.builder()
                        .aiId(ai.getId())
                        .name(ai.getName())
                        .model(modelCurrentRepository
                                .findFirstByUser_IdAndAi_Id(userId, ai.getId())
                                .getModelName())
                        .temperature(temperatureRepository
                                .findFirstByUser_IdAndAi_Id(userId, ai.getId())
                                .getTemperature())
                        .maxToken(maxTokenRepository
                                .findFirstByUser_IdAndAi_Id(userId, ai.getId())
                                .getMaxToken())
                        .modelsAvailable(modelAvailableRepository
                                .findByAi_Id(ai.getId())
                                .stream()
                                .map(ModelAvailable::getModelName)
                                .toList())
                        .build())
                .toList();
    }
}
