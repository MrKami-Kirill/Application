package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostLoginRequest implements Request {

    @JsonProperty(value = "e_mail")
    private String email;
    private String password;

    public PostLoginRequest() {

    }

    public PostLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
