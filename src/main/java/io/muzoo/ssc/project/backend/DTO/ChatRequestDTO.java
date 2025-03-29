package io.muzoo.ssc.project.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ChatRequestDTO {

    @NotNull(message = "Missing AI id.")
    private Long aiId;
}
