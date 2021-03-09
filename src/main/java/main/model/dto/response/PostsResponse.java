package main.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.Post;
import main.service.HtmlParserService;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponse implements Response {

    private int count;
    private List<PostResponse> posts;

    public PostsResponse(int count, List<Post> postsToShow, int announceLength) {
        this.count = count;
        this.posts = new ArrayList<>();
        for (Post p : postsToShow) {
            PostResponse postResponse = new PostResponse(p, announceLength);
            posts.add(postResponse);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class PostResponse {

        private int id;
        private String timestamp;
        private PostUser user;
        private String title;
        private String announce;
        private int likeCount;
        private int dislikeCount;
        private int commentCount;
        private int viewCount;

        private PostResponse(Post post, int announceLength) {
            this.id = post.getId();
            this.timestamp = String.valueOf(post.getTime().atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli() / 1000);
            this.user = new PostUser(post.getUser().getId(), post.getUser().getName());
            this.title = post.getTitle();
            String tempText = HtmlParserService.parseStringFromHtml(post.getText());
            if (tempText != null) {
                this.announce = tempText.length() < announceLength ? tempText
                        : tempText.substring(0, announceLength) + "...";
            }
            this.likeCount = (int) post.getPostVotes().stream().filter(p -> p.getValue() == 1).count();
            this.dislikeCount = (int) post.getPostVotes().stream().filter(p -> p.getValue() == -1).count();
            this.commentCount = post.getPostComments().size();
            this.viewCount = post.getViewCount();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        static class PostUser {
            private int id;
            private String name;

        }
    }
}
