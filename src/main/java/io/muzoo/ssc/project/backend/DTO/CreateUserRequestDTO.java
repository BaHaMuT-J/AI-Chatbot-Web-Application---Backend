package io.muzoo.ssc.project.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDTO {

    private String username;
    private String displayName;
    private String password;
}
