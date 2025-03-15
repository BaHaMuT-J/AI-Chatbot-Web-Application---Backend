package io.muzoo.ssc.project.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequestDTO {

    private Long chatId;
    private String prompt;
}
