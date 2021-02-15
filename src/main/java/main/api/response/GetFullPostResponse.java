package main.api.response;

import lombok.Data;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.TagToPost;

import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

@Data
public class GetFullPostResponse implements Response {

    private int id;
    private String timestamp;
    private boolean active;
    private PostUser user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<Comment> comments;
    private List<String> tags;

    public GetFullPostResponse(Post post) {
        this.id = post.getId();
        this.timestamp = String.valueOf(post.getTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000);
        this.active = post.isActive();
        this.user = new PostUser(post.getUser().getId(), post.getUser().getName());
        this.title = post.getTitle();
        this.text = post.getText();
        this.likeCount = (int) post.getPostVotes().stream().filter(p -> p.getValue() == 1).count();
        this.dislikeCount = (int) post.getPostVotes().stream().filter(p -> p.getValue() == -1).count();
        this.viewCount = post.getViewCount();
        this.comments = new LinkedList<>();
        this.tags = new LinkedList<>();
        for (PostComment postComment : post.getPostComments()) {
            int commentId = postComment.getId();
            String commentTime = String.valueOf(postComment.getTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000);
            String commentText = postComment.getText();

            int commentUserId = postComment.getUser().getId();
            String commentUserName = postComment.getUser().getName();
            String commentUserPhoto = postComment.getUser().getPhoto();
            Comment.CommentUser commentUser = new Comment.CommentUser(commentUserId, commentUserName, commentUserPhoto);

            Comment comment = new Comment(commentId, commentTime, commentText, commentUser);
            comments.add(comment);
        }

        for (TagToPost t2p : post.getTagToPosts()) {
            String tagName = t2p.getIdTag().getName();
            tags.add(tagName);
        }

    }

    @Data
    static class PostUser {
        private int id;
        private String name;

        private PostUser(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Data
    static class Comment {

        private int id;
        private String timestamp;
        private String text;
        private CommentUser user;

        public Comment(int id, String timestamp, String text, CommentUser user) {
            this.id = id;
            this.timestamp = timestamp;
            this.text = text;
            this.user = user;
        }

        @Data
        static class CommentUser {
            private int id;
            private String name;
            private String photo;

            public CommentUser(int id, String name, String photo) {
                this.id = id;
                this.name = name;
                this.photo = photo;
            }
        }
    }

}
