package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на авторизацию пользователя")
public class LoginRequest {
    @Schema(description = "E-mail")
    @JsonProperty(value = "e_mail")
    private String email;
    @Schema(description = "Пароль")
    private String password;
}
