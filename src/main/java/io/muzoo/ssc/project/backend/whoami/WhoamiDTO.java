package io.muzoo.ssc.project.backend.whoami;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.muzoo.ssc.project.backend.model.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WhoamiDTO {

    private boolean loggedIn = false;

    private String username;

    private String displayName;

    private Set<Chat> chats;
}
