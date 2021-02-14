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
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit + "," + "mode:" + mode);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "Сдвиг для отображения постов меньше 0" : "",
                    limit < 1 ? "Лимит для отображения постов меньше 1" : "",
                    !isModeValid ? "Режим '" + mode + "' для отображения постов не распознан"  : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Post> posts = new ArrayList<>();
            int count = postRepository.countAllPosts();
            log.info("Получено общее кол-во постов на сайте (" + count + ")");
            switch (mode.toLowerCase()) {
                case ("recent"):
                    posts = postRepository.getRecentPosts(offset, limit);
                    log.info("Получен список новых постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case ("popular"):
                    posts = postRepository.getPopularPosts(offset, limit);
                    log.info("Получен список популярных постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case ("best"):
                    posts = postRepository.getBestPosts(offset, limit);
                    log.info("Получен список самых обсуждаемых постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case ("early"):
                    posts = postRepository.getEarlyPosts(offset, limit);
                    log.info("Получен список старых постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
            }
            ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        }
    }

    public ResponseEntity<Response> getAllPostsByQuery(String query, int offset, int limit) {
        if (offset < 0 || limit < 1 ) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "Сдвиг для отображения постов меньше 0" : "",
                    limit < 1 ? "Лимит для отображения постов меньше 1" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Post> posts;
            int count;
            if (query != null && (query.trim().length() > 0)) {
                count = postRepository.countAllPostsByQuery(query);
                log.info("Получено общее кол-во постов на сайте (" + count + ") по строке поиска '" + query + "'");
                posts = postRepository.getAllPostsByQuery(query, offset, limit);
                log.info("Получен список постов за по строке поиска '" + query + "' для отображения: " + Arrays.toString(posts.toArray()));
                ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
                log.info("Направляется ответ на запрос /api/post/search cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
                return response;
            } else {
                count = postRepository.countAllPosts();
                log.info("Получено общее кол-во постов на сайте (" + count + ") без строки поиска");
                posts = postRepository.getRecentPosts(offset, limit);
                log.info("Получен список постов за без строки поиска для отображения: " + Arrays.toString(posts.toArray()));
                ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
                log.info("Направляется ответ на запрос /api/post/search cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
                return response;
            }

        }
    }

    public ResponseEntity<Response> getAllPostsByCalendar(Integer qYear) {
        int year = qYear == null ? LocalDateTime.now().getYear() : qYear;
        List<Post> postsByYear = postRepository.getPostsByYear(year);
        log.info("Получен список постов за " + year + " год");
        HashMap<Date, Integer> postsMap = new HashMap<>();
        for (Post p : postsByYear) {
            Date postDate = Date.valueOf(p.getTime().toLocalDate());
            Integer postCount = postsMap.getOrDefault(postDate, 0);
            postsMap.put(postDate, postCount + 1);
        }
        List<Integer> years = postRepository.getYearsWithAnyPosts();
        log.info("Получен список всех лет, за которые есть посты: " + Arrays.toString(years.toArray()));
        ResponseEntity<Response> response = new ResponseEntity<>(new GetPostByCalendarResponse(years, postsMap), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/calendar cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getAllPostsByDate(String date, int offset, int limit) {
        if (offset < 0 || limit < 1 ) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset: " + offset + "," + "limit: " + limit + ", " + "date:" + date);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "Сдвиг для отображения постов меньше 0" : "",
                    limit < 1 ? "Лимит для отображения постов меньше 1" : "",
                    date == null ? "Дата не задана" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            int count = postRepository.countAllPostsByDate(date);
            log.info("Получено общее кол-во постов на сайте (" + count + ") за дату '" + date + "'");
            List<Post> posts = postRepository.getAllPostsByDate(date, offset, limit);
            log.info("Получен список постов за '" + date + "' для отображения: " + Arrays.toString(posts.toArray()));
            ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post/byDate cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        }
    }

    public ResponseEntity<Response> getAllPostsByTag(String tag, int offset, int limit) {
        if (offset < 0 || limit < 1 ) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit + ", " + "date:" + tag);
            return new ResponseEntity<>(new BadRequestMessageResponse(
                    offset < 0 ? "Сдвиг для отображения постов меньше 0" : "",
                    limit < 1 ? "Лимит для отображения постов меньше 1" : "",
                    (tag == null || tag.equals("") || tag.isBlank()) ? "Тег не задан" : ""),
                    HttpStatus.BAD_REQUEST);
        } else {
            int count = postRepository.countAllPostsByTag(tag);
            log.info("Получено общее кол-во постов на сайте (" + count + ") по тегу '" + tag + "'");
            List<Post> posts = postRepository.getAllPostsByTag(tag, offset, limit);
            log.info("Получен список постов по тегу '" + tag + "' для отображения: " + Arrays.toString(posts.toArray()));
            ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post/byTag cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        }
    }

    public ResponseEntity<Response> getPostById(Integer id, HttpSession session) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            log.warn("Ошибка! Пост с id=" + id + " не найден");
            return new ResponseEntity<>(new BadRequestMessageResponse("Пост не найден"), HttpStatus.BAD_REQUEST);
        }
        log.info("Получен пост с id=" + post.getId());

        User user = userService.getUserBySession(session);

        if (user == null || (post.getUser() != user && !user.isModerator())) {
            if (!post.isActive() || (post.getModerationStatus() != ModerationStatus.ACCEPTED) || post.getTime().isAfter(LocalDateTime.now())) {
                log.warn("Некорректный пост " + post.toString() +  " для отображения пользователю");
                return new ResponseEntity<>(new BadRequestMessageResponse(
                        !post.isActive() ? "Пост не активный" : "",
                        post.getTime().isAfter(LocalDateTime.now()) ? "Время публикации поста еще не наступило" : "",
                        (post.getModerationStatus() != ModerationStatus.ACCEPTED) ? "Пост не одобрен модератором" : ""), HttpStatus.BAD_REQUEST);
            }
            int totalViewCount = post.getViewCount() + 1;
            post.setViewCount(totalViewCount);
            postRepository.save(post);
            log.info("Кол-во просмотров поста с id=" + id.toString() + " увеличено на 1. Итоговое кол-во просмотров " + totalViewCount);
        }

        ResponseEntity<Response> response = new ResponseEntity<>(new GetFullPostResponse(post), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/{id} cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

}
