package io.muzoo.ssc.project.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateDisplayNameRequestDTO {

    @NotBlank(message = "Missing new display name.")
    private String newDisplayName;
}
