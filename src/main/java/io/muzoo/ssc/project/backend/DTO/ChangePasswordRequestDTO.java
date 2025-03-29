package io.muzoo.ssc.project.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Missing new current password.")
    private String currentPassword;

    @NotBlank(message = "Missing new password.")
    private String newPassword;

    @NotBlank(message = "Missing confirmed password.")
    private String confirmNewPassword;
}
