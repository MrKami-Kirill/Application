package main.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import main.api.request.PostRequest;
import main.api.response.*;
import main.model.ModerationStatus;
import main.model.entity.Post;
import main.model.entity.User;
import main.model.repositories.PostRepository;
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

    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

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
    private GlobalSettingService globalSettingService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagToPostService tagToPostService;

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

    public ResponseEntity<Response> addPost(PostRequest postRequest, HttpSession session) {

        HashMap<String, String> errors = new HashMap<>();

        String timestamp = postRequest.getTimestamp();
        boolean isActive = postRequest.getActive() == 1;
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        List<String> tags = postRequest.getTags();
        ModerationStatus moderationStatus = ModerationStatus.NEW;

        boolean isTitleValid = isPostTextValid(title, postTitleMinLength);
        boolean isTextValid = isPostTextValid(text, postTextMinLength);

        if (!isTitleValid || !isTextValid) {
            if (!isTitleValid) {
                log.info("Заголовок не установлен или слишком короткий (минимальная длина - " + postTitleMinLength + " символа)");
                errors.put("title", "Заголовок не установлен или слишком короткий (минимальная длина - " + postTitleMinLength + " символа)");
            }
            if (!isTextValid) {
                log.info("Текст публикации не установлен или слишком короткий (минимальная длина - " + postTextMinLength + " символов)");
                errors.put("text", "Текст публикации не установлен или слишком короткий (минимальная длина - " + postTextMinLength + " символов)");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.OK);
        }

        LocalDateTime time = getTimeForPost(timestamp);

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            errors.put("session", "Пользователь для сессии с ID=" + session.getId() + " не найден");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

        if (user.isModerator() == 1 || (user.isModerator() == 0 && !globalSettingService.getGlobalSettingValue("POST_PREMODERATION"))) {
            moderationStatus = ModerationStatus.ACCEPTED;
        }

        Post post = new Post(isActive, moderationStatus, null, time, title, text, 0, user);
        postRepository.save(post);
        log.info(moderationStatus == ModerationStatus.ACCEPTED ? "Опубликован новый пост с ID=" + post.getId() : "Создан неопубликованный пост с ID=" + post.getId());

        if (!tags.isEmpty() && tags.size() > 0)  {
            tagService.addNewTagsByPost(tags, post);
        }
        return new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
    }

    public ResponseEntity<Response> editPost(Integer id, PostRequest postRequest, HttpSession session) {

        HashMap<String, String> errors = new HashMap<>();

        String timestamp = postRequest.getTimestamp();
        boolean isActive = postRequest.getActive() == 1;
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        List<String> tags = postRequest.getTags();
        Post post = postRepository.findById(id).orElse(null);

        if (post == null) {
            log.warn("Не найден пост с ID=" + id);
            errors.put("post", "Пост не найден");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

        boolean isTitleValid = isPostTextValid(title, postTitleMinLength);
        boolean isTextValid = isPostTextValid(text, postTextMinLength);

        if (!isTitleValid || !isTextValid) {
            if (!isTitleValid) {
                log.info("Заголовок не установлен или слишком короткий (минимальная длина - " + postTitleMinLength + " символа)");
                errors.put("title", "Заголовок не установлен или слишком короткий (минимальная длина - " + postTitleMinLength + " символа)");
            }
            if (!isTextValid) {
                log.info("Текст публикации не установлен или слишком короткий (минимальная длина - " + postTextMinLength + " символов)");
                errors.put("text", "Текст публикации не установлен или слишком короткий (минимальная длина - " + postTextMinLength + " символов)");
            }
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

        LocalDateTime time = LocalDateTime.ofEpochSecond(Long.parseLong(timestamp), 0, ZoneOffset.UTC);

        if (!isPostTimeValid(timestamp)) {
            log.warn("Ошибка! Время публикации поста раньше чем текущее время");
            time = LocalDateTime.now();
            log.info("Время публикации поста изменено на " + time);
        }

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            errors.put("session", "Пользователь для сессии с ID=" + session.getId() + " не найден");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

        if (post.getUser() != user) {
            log.warn("Пользователь с ID=" + user.getId() + " не может редактировать пост с ID=" + post.getId() + " , т.к. не является его автором");
            errors.put("user", "Пользователь не является автором поста");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

        ModerationStatus moderationStatus = post.getModerationStatus();

        if (post.getUser() == user && globalSettingService.getGlobalSettingValue("POST_PREMODERATION") && user.isModerator() == 0) {
            moderationStatus = ModerationStatus.NEW;
        }

        post.setActive(isActive);
        post.setModerationStatus(moderationStatus);
        post.setTime(time);
        post.setTitle(title);
        post.setText(text);
        postRepository.save(post);
        log.info("Данные поста с ID=" + post.getId() + "успешно изменены на {" +
                "is_active: " + post.isActive() + ", " +
                "moderation_status: " + post.getModerationStatus() + ", " +
                "time: " + post.getTime() + ", " +
                "title: " + post.getTitle() + ", " +
                "text: " + post.getText() + "}");

        tagToPostService.deleteTagToPostByPost(post);
        if (!tags.isEmpty() && tags.size() > 0) {
            tagService.addNewTagsByPost(tags, post);
        }

        return new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);

    }

    private boolean isPostTextValid(String text, int minLength) {
        if (text == null || text.isBlank() || text.equals("") || text.length() < minLength) {
            return false;
        }
        return true;
    }

    private boolean isPostTimeValid(String time) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(Long.valueOf(time), 0, ZoneOffset.UTC);
        if (time == null ||
                time.isBlank() ||
                time.equals("") ||
                ldt.isBefore(LocalDateTime.now())
        ) {
            return false;
        }
        return true;
    }

    private LocalDateTime getTimeForPost(String timestamp) {
        LocalDateTime time = LocalDateTime.ofEpochSecond(Long.parseLong(timestamp), 0, ZoneOffset.UTC);

        if (!isPostTimeValid(timestamp)) {
            log.warn("Ошибка! Время публикации поста раньше чем текущее время");
            time = LocalDateTime.now();
            log.info("Время публикации поста изменено на " + time);
        }

        return time;
    }
}
