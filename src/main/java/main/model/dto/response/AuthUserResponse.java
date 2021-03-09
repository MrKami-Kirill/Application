package main.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse implements Response  {

    private boolean result;
    private LoginUser user;

    public AuthUserResponse(User user, int moderationCount) {
        this.result = true;
        this.user = new LoginUser(user, moderationCount);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginUser {

        private int id;
        private String name;
        private String photo;
        private String email;
        private int moderation;
        private int moderationCount;
        private int settings;

        private LoginUser(User user, int moderationCount) {
            this.id = user.getId();
            this.name = user.getName();
            this.photo = user.getPhoto();
            this.email = user.getEmail();
            this.moderation = user.getIsModerator();
            this.moderationCount = moderationCount;
            this.settings = user.getIsModerator();
        }

    }
}