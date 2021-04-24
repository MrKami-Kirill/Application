package main.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на изменение профиля пользователя")
public class EditProfileRequest {

    @Schema(description = "Имя")
    private String name;
    @Schema(description = "E-mail")
    private String email;
    @Schema(description = "Пароль")
    private String password;
    @Schema(description = "Параметр, который указывает на то, что фотографию нужно удалить (если значение равно 1)")
    private Integer removePhoto;


}
