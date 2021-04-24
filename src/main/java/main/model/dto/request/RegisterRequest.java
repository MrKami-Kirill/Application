package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegisterRequest {
    @Schema(description = "E-mail")
    @JsonProperty("e_mail")
    private String email;
    @Schema(description = "Пароль")
    private String password;
    @Schema(description = "Имя")
    private String name;
    @Schema(description = "Код captcha")
    private String captcha;
    @Schema(description = "Значение секретного кода")
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
