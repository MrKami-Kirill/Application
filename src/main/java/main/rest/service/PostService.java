package main.rest.service;

import lombok.extern.log4j.Log4j2;
import main.rest.api.response.*;
import main.rest.model.ModerationStatus;
import main.rest.model.entity.Post;
import main.rest.model.entity.User;
import main.rest.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Log4j2
public class PostService {

    @Value("${post.announce.max_length}")
    private int announceLength;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public ResponseEntity<Response> getAllPosts(int offset, int limit, String mode) {

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
            int count = postRepository.countAllPosts();
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

    public ResponseEntity<Response> getAllPostsByQuery(String query, int offset, int limit) {
        if (offset < 0 || limit < 1 ) {
            log.warn("Invalid params: " +
                    "offset:" + offset + "," + "limit:" + limit);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "offset < 0" : "",
                    limit < 1 ? "limit < 1" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Post> posts = new ArrayList<>();
            int count;
            if (query != null && (query.trim().length() > 0)) {
                count = postRepository.countAllPostsByQuery(query);
                posts = postRepository.getAllPostsByQuery(query, offset, limit);
                log.info("Post's list return with parameters: {" +
                        " query: " + query +
                        " offset: " + offset +
                        " limit: " + limit + " }");
                return new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            } else {
                count = postRepository.countAllPosts();
                posts = postRepository.getRecentPosts(offset, limit);
                return new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<Response> getAllPostsByCalendar(Integer qYear) {
        int year = qYear == null ? LocalDateTime.now().getYear() : qYear;
        List<Post> postsByYear = postRepository.getPostsByYear(year);
        log.info("Return posts for " + year);
        HashMap<Date, Integer> postsMap = new HashMap<>();
        for (Post p : postsByYear) {
            Date postDate = Date.valueOf(p.getTime().toLocalDate());
            Integer postCount = postsMap.getOrDefault(postDate, 0);
            postsMap.put(postDate, postCount + 1);
        }
        List<Integer> years = postRepository.getYearsWithAnyPosts();
        log.info("Return all years for which there are posts: " + Arrays.toString(years.toArray()));
        return new ResponseEntity<>(new GetPostByCalendarResponse(years, postsMap), HttpStatus.OK);
    }

    public ResponseEntity<Response> getAllPostsByDate(String date, int offset, int limit) {
        if (offset < 0 || limit < 1 ) {
            log.warn("Invalid params: " +
                    "offset:" + offset + "," + "limit:" + limit);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "offset < 0" : "",
                    limit < 1 ? "limit < 1" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Post> posts = postRepository.getAllPostsByDate(date, offset, limit);
            int count = postRepository.countAllPostsByDate(date);
            log.info("Post's list return with parameters: {" +
                    " date: " + date +
                    " offset: " + offset +
                    " limit: " + limit + " }");
            return new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
        }
    }

    public ResponseEntity<Response> getAllPostsByTag(String tag, int offset, int limit) {
        if (offset < 0 || limit < 1 ) {
            log.warn("Invalid params: " +
                    "offset:" + offset + "," + "limit:" + limit);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "offset < 0" : "",
                    limit < 1 ? "limit < 1" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Post> posts = postRepository.getAllPostsByTag(tag, offset, limit);
            int count = postRepository.countAllPostsByTag(tag);
            log.info("Post's list return with parameters: {" +
                    " tag: " + tag +
                    " offset: " + offset +
                    " limit: " + limit + " }");
            return new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
        }
    }

    public ResponseEntity<Response> getPostById(Integer id, HttpSession session) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.warn("Post with id=" + id + " not found!");
            return new ResponseEntity<>(new BadRequestMessageResponse("Post not found!"), HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserBySession(session);

        if (user == null || (post.getUser() != user && !user.isModerator())) {
            if (!post.isActive() || (post.getModerationStatus() != ModerationStatus.ACCEPTED) || post.getTime().isAfter(LocalDateTime.now())) {
                log.warn("Incorrect post " + post.toString() +  " to display!");
                return new ResponseEntity<>(new BadRequestMessageResponse(
                        !post.isActive() ? "Invalid status for display!" : "",
                        post.getTime().isAfter(LocalDateTime.now()) ? "Invalid time to display!" : "",
                        (post.getModerationStatus() != ModerationStatus.ACCEPTED) ? "Invalid moderation status for display!" : ""), HttpStatus.BAD_REQUEST);
            }
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
            log.info("Count post's views (postId = " + id + ") has increased. Total Views = 3");
        }

        log.info("Post with id=" + id + " returned!");
        return new ResponseEntity<>(new GetFullPostResponse(post), HttpStatus.OK);
    }

}
