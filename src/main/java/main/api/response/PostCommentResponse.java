package main.api.response;

import lombok.Data;
import main.model.entity.PostComment;

@Data
public class PostCommentResponse implements Response {
    private int id;

    public PostCommentResponse() {
    }

    public PostCommentResponse(PostComment postComment) {
        this.id = postComment.getId();
    }
}
