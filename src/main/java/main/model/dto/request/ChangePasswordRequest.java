package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение пароля пользователя")
public class ChangePasswordRequest {

    @Schema(description = "Код восстановления пароля")
    private String code;
    @Schema(description = "Пароль")
    private String password;
    @Schema(description = "Код captcha")
    private String captcha;
    @Schema(description = "Значение секретного кода")
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
