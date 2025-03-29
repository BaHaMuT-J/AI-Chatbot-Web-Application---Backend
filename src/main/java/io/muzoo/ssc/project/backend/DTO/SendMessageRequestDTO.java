package io.muzoo.ssc.project.backend.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequestDTO {

    @NotNull(message = "Missing Chat id.")
    private Long chatId;

    @NotBlank(message = "Missing prompt.")
    private String prompt;
}
