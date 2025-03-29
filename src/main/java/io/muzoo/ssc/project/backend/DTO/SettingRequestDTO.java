package io.muzoo.ssc.project.backend.DTO;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SettingRequestDTO {

    @NotNull(message = "Missing User id.")
    private Long userId;

    @NotNull(message = "Missing AI id.")
    private Long aiId;

    @NotBlank(message = "Missing model name.")
    private String model;

    @DecimalMin(value = "0.1", message = "Temperature must be at least 0.1.")
    @DecimalMax(value = "0.9", message = "Temperature must be at most 0.9.")
    @NotNull(message = "Missing temperature.")
    private Double temperature;

    @Min(value = 100, message = "Max token must be at least 100.")
    @Max(value = 1000, message = "Max token must be at most 1000.")
    @NotNull(message = "Missing max token.")
    private Integer maxToken;
}
