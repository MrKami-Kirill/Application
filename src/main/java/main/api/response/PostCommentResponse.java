package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.PostComment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentResponse implements Response {
    private int id;

    public PostCommentResponse(PostComment postComment) {
        this.id = postComment.getId();
    }
}
