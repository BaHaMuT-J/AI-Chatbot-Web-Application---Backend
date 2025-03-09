package io.muzoo.ssc.project.backend.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SimpleResponseDTO {

    private boolean success;
    private String message;
}
