package io.muzoo.ssc.project.backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SendMessageResponseDTO extends SimpleResponseDTO {

    private String response;
}
