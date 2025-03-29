package io.muzoo.ssc.project.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class MessageRequestDTO {

    @NotNull(message = "Missing Chat id.")
    private Long chatId;
}
