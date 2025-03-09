package io.muzoo.ssc.project.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private String username;
    private String password;
}
