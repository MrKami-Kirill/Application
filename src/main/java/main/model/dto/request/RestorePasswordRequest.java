package main.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на восстановление пароля")
public class RestorePasswordRequest {

    @Schema(description = "E-mail")
    private String email;
}
