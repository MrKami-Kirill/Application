package main.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.TagToPost;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullPostResponse implements Response {

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

    public FullPostResponse(Post post) {
        this.id = post.getId();
        this.timestamp = String.valueOf(post.getTime().atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli() / 1000);
        this.active = post.isActive();
        this.user = new PostUser(post.getUser().getId(), post.getUser().getName());
        this.title = post.getTitle();
        this.text = post.getText();
        this.likeCount = (int) post.getPostVotes().stream().filter(p -> p.getValue() == 1).count();
        this.dislikeCount = (int) post.getPostVotes().stream().filter(p -> p.getValue() == -1).count();
        this.viewCount = post.getViewCount();
        this.comments = new LinkedList<>();
        this.tags = new LinkedList<>();
        for (PostComment postComment : post.getPostComments().stream().sorted(new CommentComparator()).collect(Collectors.toList())) {
            int commentId = postComment.getId();
            String commentTime = String.valueOf(postComment.getTime().atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli() / 1000);
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
    @NoArgsConstructor
    @AllArgsConstructor
    static class Comment {

        private int id;
        private String timestamp;
        private String text;
        private CommentUser user;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class CommentUser {
            private int id;
            private String name;
            private String photo;
        }
    }

    static class CommentComparator implements Comparator<PostComment> {

        @Override
        public int compare(PostComment o1, PostComment o2) {
            return o1.getTime().compareTo(o2.getTime());
        }
    }

}
