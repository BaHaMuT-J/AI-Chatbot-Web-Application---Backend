package io.muzoo.ssc.project.backend.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SettingRequestDTO {

    private Long userId;
    private Long aiId;
    private String model;
    private Double temperature;
    private Integer maxToken;
}
