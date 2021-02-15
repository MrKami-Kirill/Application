package main.api.response;

import lombok.Data;
import main.model.entity.User;

@Data
public class AuthUserResponse implements Response  {

    private boolean result;
    private LoginUser user;

    public AuthUserResponse(User user, int moderationCount) {
        this.result = true;
        this.user = new LoginUser(user, moderationCount);
    }

    @Data
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
            this.moderation = user.isModerator();
            this.moderationCount = moderationCount;
            this.settings = user.isModerator();
        }

    }
}