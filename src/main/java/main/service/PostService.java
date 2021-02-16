package main.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import main.api.request.PostAddPostRequest;
import main.api.response.*;
import main.model.ModerationStatus;
import main.model.entity.Post;
import main.model.entity.User;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


@Service
@Data
@Slf4j
public class PostService {

    @Value("${post.announce.max_length}")
    private int announceLength;

    @Value("${post.title.min_length}")
    private int postTitleMinLength;

    @Value("${post.text.min_length}")
    private int postTextMinLength;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Response> getAllPosts(int offset, int limit, String mode) {

        HashMap<String, String> errors = new HashMap<>();
        boolean isModeValid = List.of("recent", "popular", "best", "early").contains(mode);

        if (offset < 0 || limit < 1 || !isModeValid) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit + "," + "mode:" + mode);
            if (offset < 0) {
                errors.put("offset", "Сдвиг для отображения постов меньше 0");
            }
            if (limit < 0) {
                errors.put("limit", "Лимит для отображения постов меньше 1");
            }
            if (!isModeValid) {
                errors.put("mode", "Режим '" + mode + "' для отображения постов не распознан");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
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

        HashMap<String, String> errors = new HashMap<>();

        if (offset < 0 || limit < 1) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit);
            if (offset < 0) {
                errors.put("offset", "Сдвиг для отображения постов меньше 0");
            }
            if (limit < 0) {
                errors.put("limit", "Лимит для отображения постов меньше 1");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
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

        HashMap<String, String> errors = new HashMap<>();

        if (offset < 0 || limit < 1 || date == null) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit + ", " + "date:" + date);
            if (offset < 0) {
                errors.put("offset", "Сдвиг для отображения постов меньше 0");
            }
            if (limit < 0) {
                errors.put("limit", "Лимит для отображения постов меньше 1");
            }
            if (date == null) {
                errors.put("date", "Дата не задана");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
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

        HashMap<String, String> errors = new HashMap<>();

        if (offset < 0 || limit < 1 || (tag == null || tag.equals("") || tag.isBlank())) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit + ", " + "tag:" + tag);
            if (offset < 0) {
                errors.put("offset", "Сдвиг для отображения постов меньше 0");
            }
            if (limit < 0) {
                errors.put("limit", "Лимит для отображения постов меньше 1");
            }
            if (tag == null || tag.equals("") || tag.isBlank()) {
                errors.put("tag", "Тег не задан");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
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

    public ResponseEntity<Response> getPostById(Integer postId, HttpSession session) {

        HashMap<String, String> errors = new HashMap<>();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            log.warn("Ошибка! Пост с ID=" + postId + " не найден");
            errors.put("ID", "Пост c ID=" + postId + " не найден");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }
        log.info("Получен пост с ID=" + post.getId());

        User user = userService.getUserBySession(session);
        if (user == null || (post.getUser() != user && user.isModerator() == 1)) {
            if (!post.isActive() || (post.getModerationStatus() != ModerationStatus.ACCEPTED) || post.getTime().isAfter(LocalDateTime.now())) {
                log.warn("Некорректный пост " + post.toString() + " для отображения пользователю");
                if (!post.isActive()) {
                    errors.put("is_active","Пост не активный");
                }
                if (post.getTime().isAfter(LocalDateTime.now())) {
                    errors.put("time","Время публикации поста еще не наступило");
                }
                if (post.getModerationStatus() != ModerationStatus.ACCEPTED) {
                    errors.put("moderation_status","Пост не одобрен модератором");
                }
                return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
            }
            int totalViewCount = post.getViewCount() + 1;
            post.setViewCount(totalViewCount);
            postRepository.save(post);
            log.info("Кол-во просмотров поста с ID=" + postId.toString() + " увеличено на 1. Итоговое кол-во просмотров " + totalViewCount);
        }

        ResponseEntity<Response> response = new ResponseEntity<>(new GetFullPostResponse(post), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/{id} cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getMyPosts(String status, int offset, int limit, HttpSession session) {

        HashMap<String, String> errors = new HashMap<>();
        boolean isStatusValid = List.of("inactive", "pending", "declined", "published").contains(status);

        if (offset < 0 || limit < 1 || !isStatusValid) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit + "," + "status:" + status);
            if (offset < 0) {
                errors.put("offset", "Сдвиг для отображения постов меньше 0");
            }
            if (limit < 0) {
                errors.put("limit", "Лимит для отображения постов меньше 1");
            }
            if (!isStatusValid) {
                errors.put("status", "Задан некорректный статус '" + status + "' для отображения постов");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        } else {
            User user = userService.getUserBySession(session);
            if (user == null) {
                log.warn("Не найден пользователь для сессии с ID=" + session.getId());
                errors.put("session", "Пользователь для сессии с ID=" + session.getId() + " не найден");
                return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
            }

            Integer userId = user.getId();
            int count = 0;
            List<Post> posts = new ArrayList<>();
            switch (status) {
                case "inactive":
                    count = postRepository.countMyInActivePosts(userId, offset, limit);
                    log.info("Получено общее кол-во не опубликованных постов на сайте (" + count + ") для пользователя с ID=" + userId);
                    posts = postRepository.getMyInActivePosts(userId, offset, limit);
                    log.info("Для пользователя с ID=" + userId + " получен список не опубликованных постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case "pending":
                    count = postRepository.countMyPendingPosts(userId, offset, limit);
                    log.info("Получено общее кол-во постов, которые находятся на утверждение у модератора, на сайте (" + count + ") для пользователя с ID=" + userId);
                    posts = postRepository.getMyPendingPosts(userId, offset, limit);
                    log.info("Для пользователя с ID=" + userId + " получен список постов, которые находятся на утверждение у модератора, для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case "declined":
                    count = postRepository.countMyDeclinedPosts(userId, offset, limit);
                    log.info("Получено общее кол-во отклоненных постов на сайте (" + count + ") для пользователя с ID=" + userId);
                    posts = postRepository.getMyDeclinedPosts(userId, offset, limit);
                    log.info("Для пользователя с ID=" + userId + " получен список отклоненных постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case "published":
                    count = postRepository.countMyPublishedPosts(userId, offset, limit);
                    log.info("Получено общее кол-во опубликованных постов на сайте (" + count + ") для пользователя с ID=" + userId);
                    posts = postRepository.getMyPublishedPosts(userId, offset, limit);
                    log.info("Для пользователя с ID=" + userId + " получен список опубликованных постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
            }
            ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post/my cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        }
    }

    public ResponseEntity<Response> getAllModeratePosts(String status, int offset, int limit, HttpSession session) {
        HashMap<String, String> errors = new HashMap<>();

        if (offset < 0 || limit < 1) {
            log.warn("Некорректные параметры для отображения постов: " +
                    "offset:" + offset + "," + "limit:" + limit);
            if (offset < 0) {
                errors.put("offset", "Сдвиг для отображения постов меньше 0");
            }
            if (limit < 0) {
                errors.put("limit", "Лимит для отображения постов меньше 1");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        } else {
            User moderator = userService.getUserBySession(session);
            if (moderator == null) {
                log.warn("Не найден пользователь для сессии с ID=" + session.getId());
                errors.put("session", "Пользователь для сессии с ID=" + session.getId() + " не найден");
                return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
            }

            if (moderator.isModerator() == 0) {
                log.info("Для данного действия пользователю c ID=" + moderator.getId() + " требуются права модератора");
                errors.put("is_moderator", "Требуются права модератора");
                return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
            }

            Integer moderatorId = moderator.getId();
            int count = 0;
            List<Post> posts = new ArrayList<>();

            switch (status.toLowerCase()) {
                case ("new"):
                    count = postRepository.countAllModeratePosts(offset, limit);
                    log.info("Получено общее кол-во постов на сайте (" + count + ") для модератора с ID=" + moderatorId);
                    posts = postRepository.getAllModeratePosts(offset, limit);
                    log.info("Для модератора с ID=" + moderatorId + " получен список постов для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case ("declined"):
                    count = postRepository.countAllModeratePostsByMe("DECLINED", moderatorId, offset, limit);
                    log.info("Получено общее кол-во постов в статусе 'DECLINED' на сайте (" + count + ") для модератора с ID=" + moderatorId);
                    posts = postRepository.getAllModeratePostsByMe("DECLINED", moderatorId, offset, limit);
                    log.info("Для модератора с ID=" + moderatorId + " получен список постов в статусе 'DECLINED' для отображения: " + Arrays.toString(posts.toArray()));
                    break;
                case ("accepted"):
                    count = postRepository.countAllModeratePostsByMe("ACCEPTED", moderatorId, offset, limit);
                    log.info("Получено общее кол-во постов в статусе 'ACCEPTED' на сайте (" + count + ") для модератора с ID=" + moderatorId);
                    posts = postRepository.getAllModeratePostsByMe("ACCEPTED", moderatorId, offset, limit);
                    log.info("Для модератора с ID=" + moderatorId + " получен список постов в статусе 'ACCEPTED' для отображения: " + Arrays.toString(posts.toArray()));
                    break;
            }

            ResponseEntity<Response> response = new ResponseEntity<>(new GetPostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post/moderation cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        }
    }

    public ResponseEntity<Response> addPost(PostAddPostRequest addPostRequest, HttpSession session) {

        HashMap<String, String> errors = new HashMap<>();

        String timestamp = addPostRequest.getTimestamp();
        int active = addPostRequest.getActive();
        String title = addPostRequest.getTitle();
        String text = addPostRequest.getText();
        List<String> tags = addPostRequest.getTags();

        boolean isTitleValid = isPostTextValid(title, postTitleMinLength);
        boolean isTextValid = isPostTextValid(text, postTextMinLength);

        if (!isTitleValid || !isTextValid) {
            if (!isTitleValid) {
                log.info("Заголовок не установлен или слишком короткий (минимальная длина - " + postTitleMinLength + " символа");
                errors.put("text", "Заголовок не установлен или слишком короткий (минимальная длина - " + postTitleMinLength + " символа");
            }
            if (!isTitleValid) {
                log.info("Текст публикации не установлен или слишком короткий (минимальная длина - " + postTextMinLength + " символа");
                errors.put("text", "Текст публикации не установлен или слишком короткий (минимальная длина - " + postTextMinLength + " символа");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.OK);
        }
        if (!isPostTimeValid(timestamp)) {

        }

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            errors.put("session", "Пользователь для сессии с ID=" + session.getId() + " не найден");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }



        return null;
    }

    private boolean isPostTextValid(String text, int minLength) {
        if (text == null || text.isBlank() || text.equals("") || text.length() < minLength) {
            return false;
        }
        return true;
    }

    private boolean isPostTimeValid(String time) {
        if (time == null ||
                time.isBlank() ||
                time.equals("") ||
                time.equals(String.valueOf(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000))
        ) {
            return false;
        }
        return true;
    }


}
