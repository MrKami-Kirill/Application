package main.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление/ изменение поста")
public class PostRequest {
    @Schema(description = "Дата и время публикации в формате UTC")
    private String timestamp;
    @Schema(description = "1 или 0, открыт пост или скрыт")
    private int active;
    @Schema(description = "Заголовок поста")
    private String title;
    @Schema(description = "Тэги")
    private List<String> tags;
    @Schema(description = "Текст поста в формате HTML")
    private String text;
}
