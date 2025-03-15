package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.model.AI;
import io.muzoo.ssc.project.backend.model.Chat;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.AIRepository;
import io.muzoo.ssc.project.backend.repository.ChatRepository;
import io.muzoo.ssc.project.backend.repository.UserRepository;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User admin = userRepository.findFirstByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("a"));
            admin.setDisplayName("admin");
            userRepository.save(admin);

            List<AI> ais = aiRepository.findAll();
            for (AI ai : ais) {
                Chat chat = new Chat();
                chat.setUser(admin);
                chat.setAi(ai);
                chatRepository.save(chat);
            }
        }

        AI gemini = aiRepository.findFirstByName("Gemini");
        if (gemini == null) {
            gemini = new AI();
            gemini.setName("Gemini");
            gemini.setVersion("1");
            gemini.setApiLink(String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s",
                    Dotenv.load().get("GEMINI_API_KEY")
            ));
            aiRepository.save(gemini);
        }
    }
}
