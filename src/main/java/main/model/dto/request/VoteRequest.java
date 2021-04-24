package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на проставление лайка/ дислайка к посту")
public class VoteRequest {
    @Schema(description = "ID поста")
    @JsonProperty("post_id")
    private int postId;
}
