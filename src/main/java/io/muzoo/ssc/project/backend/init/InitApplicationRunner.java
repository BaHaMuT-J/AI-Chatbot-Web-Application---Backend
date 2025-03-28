package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.model.*;
import io.muzoo.ssc.project.backend.repository.*;
import io.muzoo.ssc.project.backend.service.CreateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIRepository aiRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelCurrentRepository modelCurrentRepository;

    @Autowired
    private ModelAvailableRepository modelAvailableRepository;

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private MaxTokenRepository maxTokenRepository;

    @Autowired
    private CreateUserService createUserService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Dotenv dotenv = Dotenv.load();
        AI gemini = aiRepository.findFirstByName("Gemini");
        if (gemini == null) {
            gemini = new AI();
            gemini.setName("Gemini");
            gemini.setApiLink(String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s",
                    dotenv.get("GEMINI_API_KEY")
            ));
            aiRepository.save(gemini);

            ModelAvailable modelAvailable = new ModelAvailable();
            modelAvailable.setAi(gemini);
            modelAvailable.setModelName("2.0 Flash");
            modelAvailableRepository.save(modelAvailable);
        }

        AI groq = aiRepository.findFirstByName("groq");
        if (groq == null) {
            groq = new AI();
            groq.setName("groq");
            groq.setApiLink("https://api.groq.com/openai");
            aiRepository.save(groq);

            List<String> models = List.of("llama3-70b-8192", "llama3-8b-8192", "mixtral-8x7b-32768", "gemma2-9b-it");
            for (String model : models) {
                ModelAvailable modelAvailable = new ModelAvailable();
                modelAvailable.setAi(groq);
                modelAvailable.setModelName(model);
                modelAvailableRepository.save(modelAvailable);
            }
        }

        AI deepseek = aiRepository.findFirstByName("DeepSeek");
        if (deepseek == null) {
            deepseek = new AI();
            deepseek.setName("DeepSeek");
            deepseek.setApiLink("https://api.deepseek.com");
            aiRepository.save(deepseek);

            List<String> models = List.of("deepseek-chat", "deepseek-reasoner");
            for (String model : models) {
                ModelAvailable modelAvailable = new ModelAvailable();
                modelAvailable.setAi(deepseek);
                modelAvailable.setModelName(model);
                modelAvailableRepository.save(modelAvailable);
            }
        }

        AI mistral = aiRepository.findFirstByName("Mistral");
        if (mistral == null) {
            mistral = new AI();
            mistral.setName("Mistral");
            mistral.setApiLink("https://api.mistral.ai/");
            aiRepository.save(mistral);

            List<String> models = List.of("mistral-small-latest", "mistral-large-latest", "open-mistral-7b", "open-mixtral-8x7b", "open-mixtral-8x22b");
            for (String model : models) {
                ModelAvailable modelAvailable = new ModelAvailable();
                modelAvailable.setAi(mistral);
                modelAvailable.setModelName(model);
                modelAvailableRepository.save(modelAvailable);
            }
        }

        User admin = userRepository.findFirstByUsername("admin");
        if (admin == null) {
            createUserService.createUser("admin", "admin", "a");
        } else {
            createUserService.setupUserAI(admin);
        }
    }
}
