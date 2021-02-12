package main.rest.service;

import lombok.extern.log4j.Log4j2;
import main.rest.api.response.BadRequestMessageResponse;
import main.rest.api.response.GetPostsResponse;
import main.rest.api.response.Response;
import main.rest.model.entity.Post;
import main.rest.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Log4j2
public class PostService {

    @Value("${post.announce.max_length}")
    private int announceLength;

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<Response> getPostsWithParams(int offset, int limit, String mode) {

        boolean isModeValid = List.of("recent", "popular", "best", "early").contains(mode);
        if (offset < 0 || limit < 1 || !isModeValid) {
            log.warn("Invalid params: " +
                    "offset:" + offset + "," + "limit:" + limit + "," + "mode:" + mode);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "offset < 0" : "",
                    limit < 1 ? "limit < 1" : "",
                    !isModeValid ? "Unexpected mode value" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Post> posts = new ArrayList<>();
            int count = postRepository.countAllPostsAtSite();
            switch (mode.toLowerCase()) {
                case ("recent"):
                    posts = postRepository.getRecentPosts(offset, limit);
                    break;
                case ("popular"):
                    posts = postRepository.getPopularPosts(offset, limit);
                    break;
                case ("best"):
                    posts = postRepository.getBestPosts(offset, limit);
                    break;
                case ("early"):
                    posts = postRepository.getEarlyPosts(offset, limit);
                    break;
            }
            log.info("Post's list return with parameters: {" +
                    " mode: " + mode +
                    " offset: " + offset +
                    " limit: " + limit + " }");
            return new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
        }
    }

}
