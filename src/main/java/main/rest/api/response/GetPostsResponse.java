package main.rest.api.response;

import lombok.Data;
import main.rest.model.entity.Post;
import main.rest.service.HtmlParserService;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetPostsResponse implements Response {

    private int count;
    private List<PostResponse> posts;

    public GetPostsResponse(int count, List<Post> postsToShow, int announceLength) {
        this.count = count;
        this.posts = new ArrayList<>();
        for (Post p : postsToShow) {
            PostResponse postResponse = new PostResponse(p, announceLength);
            posts.add(postResponse);
        }
    }

    @Data
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
            this.timestamp = String.valueOf(post.getTime().atZone(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000);
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
        static class PostUser {
            private int id;
            private String name;

            private PostUser(int id, String name) {
                this.id = id;
                this.name = name;
            }
        }
    }
}
