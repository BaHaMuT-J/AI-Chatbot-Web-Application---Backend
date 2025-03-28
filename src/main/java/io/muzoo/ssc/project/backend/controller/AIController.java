package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.AIDTO;
import io.muzoo.ssc.project.backend.DTO.SettingRequestDTO;
import io.muzoo.ssc.project.backend.DTO.SettingResponseDTO;
import io.muzoo.ssc.project.backend.DTO.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.model.*;
import io.muzoo.ssc.project.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

    @PostMapping("/api/ai/setting")
    public SimpleResponseDTO setting(@RequestBody SettingRequestDTO settingRequestDTO) {
        Long userId = settingRequestDTO.getUserId();
        Long aiId = settingRequestDTO.getAiId();

        // Validate setting owner and current logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            User currentUser = userRepository.findFirstByUsername(user.getUsername());
            if (!Objects.equals(userId, currentUser.getId())) {
                return SettingResponseDTO.builder()
                        .success(false)
                        .message("You cannot modify setting other user value.")
                        .oldModel(modelCurrentRepository
                                .findFirstByUser_IdAndAi_Id(userId, aiId)
                                .getModelName())
                        .oldTemperature(temperatureRepository
                                .findFirstByUser_IdAndAi_Id(userId, aiId)
                                .getTemperature())
                        .oldMaxToken(maxTokenRepository
                                .findFirstByUser_IdAndAi_Id(userId, aiId)
                                .getMaxToken())
                        .build();
            }
        }

        ModelCurrent modelCurrent = modelCurrentRepository.findFirstByUser_IdAndAi_Id(userId, aiId);
        modelCurrent.setModelName(settingRequestDTO.getModel());
        modelCurrentRepository.save(modelCurrent);

        Temperature temperature = temperatureRepository.findFirstByUser_IdAndAi_Id(userId, aiId);
        temperature.setTemperature(settingRequestDTO.getTemperature());
        temperatureRepository.save(temperature);

        MaxToken maxToken = maxTokenRepository.findFirstByUser_IdAndAi_Id(userId, aiId);
        maxToken.setMaxToken(settingRequestDTO.getMaxToken());
        maxTokenRepository.save(maxToken);

        return SimpleResponseDTO.builder().success(true).message("Setting successful.").build();
    }
}
