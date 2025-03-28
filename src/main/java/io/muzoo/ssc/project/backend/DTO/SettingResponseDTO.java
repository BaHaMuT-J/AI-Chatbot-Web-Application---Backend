package io.muzoo.ssc.project.backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SettingResponseDTO extends SimpleResponseDTO {

    private String oldModel;
    private Double oldTemperature;
    private Integer oldMaxToken;
}
