package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostCommentRequest implements Request {

    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("post_id")
    private Integer postId;
    private String text;

    public PostCommentRequest() {
    }

    public PostCommentRequest(Integer parentId, Integer postId, String text) {
        this.parentId = parentId;
        this.postId = postId;
        this.text = text;
    }
}
