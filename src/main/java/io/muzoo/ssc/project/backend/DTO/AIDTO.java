package io.muzoo.ssc.project.backend.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AIDTO {

    private Long aiId;
    private String name;
    private String model;
    private Double temperature;
    private Integer maxToken;
    private List<String> modelsAvailable;
}
