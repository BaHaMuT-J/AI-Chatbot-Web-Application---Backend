package io.muzoo.ssc.project.backend.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageDTO {

    private boolean isUser;
    private String text;
}
