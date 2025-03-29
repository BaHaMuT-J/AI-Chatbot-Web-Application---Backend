package io.muzoo.ssc.project.backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChatMessageResponseDTO extends SimpleResponseDTO {

    private List<MessageDTO> messagesList;
}
