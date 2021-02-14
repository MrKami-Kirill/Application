package main.rest.api.response;

import lombok.Data;

@Data
public class GetCaptchaCodeResponse implements Response {

    private String secret;
    private String image;

    public GetCaptchaCodeResponse(String secret, String image) {
        this.secret = secret;
        this.image = image;
    }
}
