package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.WhoamiDTO;
import io.muzoo.ssc.project.backend.model.User;
import io.muzoo.ssc.project.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to retrieve current logged-in user.
 */
@RestController
public class WhoamiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/whoami")
    public WhoamiDTO whoami() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User user) {
                User currentUser = userRepository.findFirstByUsername(user.getUsername());
                return WhoamiDTO.builder()
                        .loggedIn(true)
                        .userId(currentUser.getId())
                        .username(currentUser.getUsername())
                        .displayName(currentUser.getDisplayName())
                        .build();
            } else {
                return WhoamiDTO.builder()
                        .loggedIn(false)
                        .build();
            }
        } catch (Exception e) {
            return WhoamiDTO.builder()
                    .loggedIn(false)
                    .build();
        }
    }

}
