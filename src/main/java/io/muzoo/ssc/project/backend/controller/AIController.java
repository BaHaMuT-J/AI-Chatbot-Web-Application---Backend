package io.muzoo.ssc.project.backend.controller;

import io.muzoo.ssc.project.backend.DTO.AIDTO;
import io.muzoo.ssc.project.backend.repository.AIRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AIController {

    @Autowired
    private AIRepository aiRepository;

    @GetMapping("/api/ai/models")
    public List<AIDTO> getModels(HttpServletRequest request) {
        return aiRepository
                .findAll()
                .stream()
                .map(ai -> AIDTO.builder().aiId(ai.getId()).name(ai.getName()).version(ai.getVersion()).build())
                .toList();
    }
}
