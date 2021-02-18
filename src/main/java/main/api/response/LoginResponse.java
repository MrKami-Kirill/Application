package main.api.response;

import lombok.Data;
import main.model.entity.User;

@Data
public class LoginResponse implements Response {
    private boolean result;
    private LoginUser user;

    public LoginResponse() {

    }

    public LoginResponse(User user, int moderationCount) {
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

        public LoginUser(User user, int moderationCount) {
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
