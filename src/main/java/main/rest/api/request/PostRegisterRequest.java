package main.rest.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRegisterRequest implements Request {
    @JsonProperty("e_mail")
    private String email;
    private String password;
    private String name;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public PostRegisterRequest() {
    }

    public PostRegisterRequest(String email, String password, String name, String captcha, String captchaSecret) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.captcha = captcha;
        this.captchaSecret = captchaSecret;
    }
}
