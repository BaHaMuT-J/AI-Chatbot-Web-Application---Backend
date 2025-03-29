package io.muzoo.ssc.project.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Missing username.")
    private String username;

    @NotBlank(message = "Missing password.")
    private String password;
}
