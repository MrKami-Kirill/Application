package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на модерацию поста")
public class PostModerationRequest {
    @Schema(description = "ID поста")
    @JsonProperty("post_id")
    private int postId;
    @Schema(description = "Решение по посту: \"accept\" или \"decline\"")
    private String decision;
}
