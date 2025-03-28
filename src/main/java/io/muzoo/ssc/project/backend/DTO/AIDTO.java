package io.muzoo.ssc.project.backend.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AIDTO {

    private Long aiId;
    private String name;
    private String model;
}
