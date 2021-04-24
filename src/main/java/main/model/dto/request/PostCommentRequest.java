package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление комментария к посту")
public class PostCommentRequest {

    @Schema(description = "ID комментария, на который пишется ответ")
    @JsonProperty("parent_id")
    private Integer parentId;
    @Schema(description = "ID поста, к которому пишется ответ")
    @JsonProperty("post_id")
    private Integer postId;
    @Schema(description = "Текст комментария (формат HTML)")
    private String text;
}
