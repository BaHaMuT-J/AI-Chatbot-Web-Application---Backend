package io.muzoo.ssc.project.backend.service;

import io.muzoo.ssc.project.backend.DTO.UserDTO;
import io.muzoo.ssc.project.backend.model.*;
import io.muzoo.ssc.project.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIRepository aiRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private MaxTokenRepository maxTokenRepository;

    @Autowired
    private ModelCurrentRepository modelCurrentRepository;

    @Autowired
    private ModelAvailableRepository modelAvailableRepository;

    public User createUser(String username, String displayName, String password) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        setupUserAI(user);

        return user;
    }

    public void setupUserAI(User user) {
        List<AI> ais = aiRepository.findAll();

        for (AI ai : ais) {
            if (chatRepository.findFirstByUser_IdAndAi_Id(user.getId(), ai.getId()) != null) {
                continue;
            }

            Chat chat = new Chat();
            chat.setUser(user);
            chat.setAi(ai);
            chatRepository.save(chat);

            Temperature temperature = new Temperature();
            temperature.setTemperature(0.8);
            temperature.setUser(user);
            temperature.setAi(ai);
            temperatureRepository.save(temperature);

            MaxToken maxToken = new MaxToken();
            maxToken.setMaxToken(200);
            maxToken.setUser(user);
            maxToken.setAi(ai);
            maxTokenRepository.save(maxToken);

            ModelCurrent modelCurrent = new ModelCurrent();
            modelCurrent.setModelName(modelAvailableRepository.findFirstByAi_Id(ai.getId()).getModelName());
            modelCurrent.setUser(user);
            modelCurrent.setAi(ai);
            modelCurrentRepository.save(modelCurrent);
        }
    }

    public UserDTO createUserAndReturnDTO(String username, String displayName, String password) {
        User user = createUser(username, displayName, password);
        return UserDTO.builder()
                .success(true)
                .message("You are successfully registered. Please login.")
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .build();
    }
}
