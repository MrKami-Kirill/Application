package main.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import main.api.request.PostModerationRequest;
import main.api.request.PostRequest;
import main.api.request.VoteRequest;
import main.api.response.*;
import main.model.ModerationStatus;
import main.model.entity.Post;
import main.model.entity.User;
import main.model.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


@Service
@Slf4j
@Data
public class PostService {

    @Value("${post.announce.max_length}")
    private int announceLength;

    @Value("${post.title.min_length}")
    private int postTitleMinLength;

    @Value("${post.text.min_length}")
    private int postTextMinLength;

    @Value("${post.image.upload_dir}")
    private String uploadDir;

    @Value("${post.image.format}")
    private String format;

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

    @Autowired
    private FileService fileService;

    @Autowired
    private PostVoteService postVoteService;

    public ResponseEntity<Response> getAllPosts(Integer  offset, Integer limit, String mode) {

        List<Post> posts;
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
            default:
                throw new IllegalStateException("Режим '" + mode  + "' для отображения постов не распознан");

        }
        ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getAllPostsByQuery(String query, Integer offset, Integer limit) {

        List<Post> posts;
        int count;
        if (query != null && (query.trim().length() > 0)) {
            count = postRepository.countAllPostsByQuery(query);
            log.info("Получено общее кол-во постов на сайте (" + count + ") по строке поиска '" + query + "'");
            posts = postRepository.getAllPostsByQuery(query, offset, limit);
            log.info("Получен список постов за по строке поиска '" + query + "' для отображения: " + Arrays.toString(posts.toArray()));
            ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post/search cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
        } else {
            count = postRepository.countAllPosts();
            log.info("Получено общее кол-во постов на сайте (" + count + ") без строки поиска");
            posts = postRepository.getRecentPosts(offset, limit);
            log.info("Получен список постов за без строки поиска для отображения: " + Arrays.toString(posts.toArray()));
            ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
            log.info("Направляется ответ на запрос /api/post/search cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
            return response;
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
        ResponseEntity<Response> response = new ResponseEntity<>(new PostByCalendarResponse(years, postsMap), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/calendar cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getAllPostsByDate(String date, Integer offset, Integer limit) {

        int count = postRepository.countAllPostsByDate(date);
        log.info("Получено общее кол-во постов на сайте (" + count + ") за дату '" + date + "'");
        List<Post> posts = postRepository.getAllPostsByDate(date, offset, limit);
        log.info("Получен список постов за '" + date + "' для отображения: " + Arrays.toString(posts.toArray()));
        ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/byDate cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getAllPostsByTag(String tag, Integer offset, Integer limit) {

        int count = postRepository.countAllPostsByTag(tag);
        log.info("Получено общее кол-во постов на сайте (" + count + ") по тегу '" + tag + "'");
        List<Post> posts = postRepository.getAllPostsByTag(tag,
                offset, limit
                //PageRequest.of(offset, limit, Sort.by("time").descending())
        );
        log.info("Получен список постов по тегу '" + tag + "' для отображения: " + Arrays.toString(posts.toArray()));
        ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/byTag cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getPostById(Integer postId, HttpSession session) throws Exception {

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            log.warn("Ошибка! Пост с ID=" + postId + " не найден");
            throw new Exception("Пост не найден");
        }
        log.info("Получен пост с ID=" + post.getId());

        User user = userService.getUserBySession(session);
        if (user == null || (post.getUser() != user && user.getIsModerator() == 0)) {
            if (!post.isActive() || (post.getModerationStatus() != ModerationStatus.ACCEPTED) || post.getTime().isAfter(LocalDateTime.now())) {
                log.warn("Некорректный пост " + post.toString() + " для отображения пользователю");
                if (!post.isActive()) {
                    throw new Exception("Пост не активный");
                }
                if (post.getTime().isAfter(LocalDateTime.now())) {
                    throw new Exception("Время публикации поста еще не наступило");
                }
                if (post.getModerationStatus() != ModerationStatus.ACCEPTED) {
                    throw new Exception("Пост не одобрен модератором");
                }
            }
            int totalViewCount = post.getViewCount() + 1;
            post.setViewCount(totalViewCount);
            postRepository.save(post);
            log.info("Кол-во просмотров поста с ID=" + postId.toString() + " увеличено на 1. Итоговое кол-во просмотров " + totalViewCount);
        }

        ResponseEntity<Response> response = new ResponseEntity<>(new FullPostResponse(post), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/{id} cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getMyPosts(String status, Integer offset, Integer limit, HttpSession session) throws Exception {

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        Integer userId = user.getId();
        int count;
        List<Post> posts;
        switch (status) {
            case "inactive":
                count = postRepository.countMyInActivePosts(userId);
                log.info("Получено общее кол-во не опубликованных постов на сайте (" + count + ") для пользователя с ID=" + userId);
                posts = postRepository.getMyInActivePosts(userId, offset, limit);
                log.info("Для пользователя с ID=" + userId + " получен список не опубликованных постов для отображения: " + Arrays.toString(posts.toArray()));
                break;
            case "pending":
                count = postRepository.countMyPendingPosts(userId);
                log.info("Получено общее кол-во постов, которые находятся на утверждение у модератора, на сайте (" + count + ") для пользователя с ID=" + userId);
                posts = postRepository.getMyPendingPosts(userId, offset, limit);
                log.info("Для пользователя с ID=" + userId + " получен список постов, которые находятся на утверждение у модератора, для отображения: " + Arrays.toString(posts.toArray()));
                break;
            case "declined":
                count = postRepository.countMyDeclinedPosts(userId);
                log.info("Получено общее кол-во отклоненных постов на сайте (" + count + ") для пользователя с ID=" + userId);
                posts = postRepository.getMyDeclinedPosts(userId, offset, limit);
                log.info("Для пользователя с ID=" + userId + " получен список отклоненных постов для отображения: " + Arrays.toString(posts.toArray()));
                break;
            case "published":
                count = postRepository.countMyPublishedPosts(userId);
                log.info("Получено общее кол-во опубликованных постов на сайте (" + count + ") для пользователя с ID=" + userId);
                posts = postRepository.getMyPublishedPosts(userId, offset, limit);
                log.info("Для пользователя с ID=" + userId + " получен список опубликованных постов для отображения: " + Arrays.toString(posts.toArray()));
                break;
            default:
                throw new IllegalStateException("Статус '" + status  + "' для отображения постов не распознан");
        }
        ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/my cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getAllModeratePosts(String status, Integer offset, Integer limit, HttpSession session) throws Exception {
        User moderator = userService.getUserBySession(session);
        if (moderator == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }
        if (moderator.getIsModerator() == 0) {
            log.info("Для данного действия пользователю c ID=" + moderator.getId() + " требуются права модератора");
            throw new Exception("Требуются права модератора");
        }

        Integer moderatorId = moderator.getId();
        int count;
        List<Post> posts;

        switch (status.toLowerCase()) {
            case ("new"):
                count = postRepository.countAllModeratePosts();
                log.info("Получено общее кол-во постов на сайте (" + count + ") для модератора с ID=" + moderatorId);
                posts = postRepository.getAllModeratePosts(offset, limit);
                log.info("Для модератора с ID=" + moderatorId + " получен список постов для отображения: " + Arrays.toString(posts.toArray()));
                break;
            case ("declined"):
                count = postRepository.countAllModeratePostsByMe("DECLINED", moderatorId);
                log.info("Получено общее кол-во постов в статусе 'DECLINED' на сайте (" + count + ") для модератора с ID=" + moderatorId);
                posts = postRepository.getAllModeratePostsByMe("DECLINED", moderatorId, offset, limit);
                log.info("Для модератора с ID=" + moderatorId + " получен список постов в статусе 'DECLINED' для отображения: " + Arrays.toString(posts.toArray()));
                break;
            case ("accepted"):
                count = postRepository.countAllModeratePostsByMe("ACCEPTED", moderatorId);
                log.info("Получено общее кол-во постов в статусе 'ACCEPTED' на сайте (" + count + ") для модератора с ID=" + moderatorId);
                posts = postRepository.getAllModeratePostsByMe("ACCEPTED", moderatorId, offset, limit);
                log.info("Для модератора с ID=" + moderatorId + " получен список постов в статусе 'ACCEPTED' для отображения: " + Arrays.toString(posts.toArray()));
                break;
            default:
                throw new IllegalStateException("Статус '" + status + "' для отображения постов не распознан");
        }

        ResponseEntity<Response> response = new ResponseEntity<>(new PostsResponse(count, posts, announceLength), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/post/moderation cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> addPost(PostRequest postRequest, HttpSession session) throws Exception{

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
            throw new Exception("Пользователь не найден");
        }

        if (user.getIsModerator() == 1 || (user.getIsModerator() == 0 && !globalSettingService.getGlobalSettingValue(GlobalSettingService.POST_PREMODERATION_CODE))) {
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

    public ResponseEntity<Response> editPost(Integer id, PostRequest postRequest, HttpSession session) throws Exception {

        HashMap<String, String> errors = new HashMap<>();

        String timestamp = postRequest.getTimestamp();
        boolean isActive = postRequest.getActive() == 1;
        String title = postRequest.getTitle();
        String text = postRequest.getText();
        List<String> tags = postRequest.getTags();
        Post post = postRepository.findById(id).orElse(null);

        if (post == null) {
            log.warn("Не найден пост с ID=" + id);
            throw new Exception("Пост не найден");
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
            throw new Exception("Пользователь не найден");
        }

        if (post.getUser() != user && user.getIsModerator() == 0) {
            log.warn("Пользователь с ID=" + user.getId() + " не может редактировать пост с ID=" + post.getId() + " , т.к. не является его автором");
            errors.put("user", "Пользователь не является автором поста");
            throw  new Exception("Пользователь не является автором поста");
        }

        ModerationStatus moderationStatus = post.getModerationStatus();

        if (post.getUser() == user && globalSettingService.getGlobalSettingValue(GlobalSettingService.POST_PREMODERATION_CODE) && user.getIsModerator() == 0) {
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

    public ResponseEntity<Response> moderation(PostModerationRequest moderationRequest, HttpSession session) throws Exception {

        int postId = moderationRequest.getPostId();
        String decision = moderationRequest.getDecision();

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            log.warn("Ошибка! Пост с ID=" + postId + " не найден");
            throw new Exception("Пост не найден");
        }

        ResponseEntity<Response> response = new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);;

        if (user.getIsModerator() == 1) {
            switch (decision) {
                case "accept":
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                    post.setModeratorId(user.getId());
                    postRepository.save(post);
                    log.info("Статус поста с ID=" + post.getId() + " изменился на 'ACCEPTED' модератором с ID=" + user.getId());
                    break;
                case "decline":
                    post.setModerationStatus(ModerationStatus.DECLINED);
                    post.setModeratorId(user.getId());
                    postRepository.save(post);
                    log.info("Статус поста с ID=" + post.getId() + " изменился на 'DECLINED' модератором с ID=" + user.getId());
                    break;
                default:
                    throw new IllegalStateException("Решение '" + decision + "' не распознано");
            }
        } else {
            log.warn("Утверждать или отклонять посты может только пользователь с правами модератора");
            throw new Exception("Пользователь не является модератором");
        }
        return response;
    }

    public ResponseEntity<?> uploadImage(MultipartFile file, HttpSession session) throws Exception {

        if (file == null) {
            log.warn("Изображение для загрузки на сервер отсутствует");
            throw new Exception("Изображение для загрузки на сервер отсутствует");
        }

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        String responsePath = fileService.createFile(uploadDir, format, file);
        ResponseEntity<String> response = new ResponseEntity<>(responsePath, HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/image cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getMyStatistics(HttpSession session) throws Exception {
        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        int userId = user.getId();
        int postsCount = postRepository.countMyPosts(userId);
        log.info("Получено общее кол-во постов (" + postsCount + ") для пользователя с ID: " + userId);
        int likesCount = postVoteService.countMyLikes(userId);
        int dislikesCount = postVoteService.countMyDislikes(userId);
        int viewsCount = postRepository.countMyViews(userId);
        log.info("Получено общее кол-во просмотров (" + viewsCount + ") для пользователя с ID: " + userId);
        LocalDateTime fistPublicationTime = postRepository.getMyFirsPublicationTime(userId, PageRequest.of(0, 1, Sort.by("time").ascending()));
        log.info("Получено время первой публикации (" + fistPublicationTime + ") для пользователя с ID: " + userId);
        String firstPublication = parseTimeToStringUTCFormat(fistPublicationTime);
        ResponseEntity<Response> response = new ResponseEntity<>(new StatisticsResponse(postsCount, likesCount, dislikesCount, viewsCount, firstPublication), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/statistics/my cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> getAllStatistics() {

        if (!globalSettingService.getGlobalSettingValue(GlobalSettingService.STATISTICS_IS_PUBLIC_CODE)) {
            log.warn("Получение всей статистики по сайту невозможно, т.к. глобальная настройка сайта STATISTICS_IS_PUBLIC включена");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        int postsCount = postRepository.countAllPosts();
        log.info("Получено общее кол-во постов (" + postsCount + ") на сайте");
        int likesCount = postVoteService.countLikes();
        int dislikesCount = postVoteService.countDislikes();
        int viewsCount = postRepository.countViews();
        log.info("Получено общее кол-во просмотров (" + viewsCount + ") на сайте");
        LocalDateTime fistPublicationTime = postRepository.getFirsPublicationTime(PageRequest.of(0, 1, Sort.by("time").ascending()));
        log.info("Получено время первой публикации (" + fistPublicationTime + ") на сайте ");
        String firstPublication = parseTimeToStringUTCFormat(fistPublicationTime);
        ResponseEntity<Response> response = new ResponseEntity<>(new StatisticsResponse(postsCount, likesCount, dislikesCount, viewsCount, firstPublication), HttpStatus.OK);
        log.info("Направляется ответ на запрос /api/statistics/all cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    public ResponseEntity<Response> vote(VoteRequest voteRequest, HttpSession session, byte value) throws Exception {
        int postId = voteRequest.getPostId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            log.warn("Ошибка! Пост с ID=" + postId + " не найден");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<Response> response;
        String strValue = value == 1 ? "like" : "dislike";
        if (postVoteService.vote(user, post, value)) {
            response = new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new BooleanResponse(false), HttpStatus.OK);
        }
        log.info("Направляется ответ на запрос /api/statistics/" + strValue + " cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    private boolean isPostTextValid(String text, int minLength) {
        if (text == null || text.isBlank() || text.equals("") || text.length() < minLength) {
            return false;
        }
        return true;
    }

    private String parseTimeToStringUTCFormat(LocalDateTime time) {
        return String.valueOf(time.atZone(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000);
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

    public int countAllPostsForModeration() {
        return postRepository.countAllPostsForModeration();
    }

    public int countAllPosts() {
        return postRepository.countAllPosts();
    }

    public int countAllPostsByTagId(int tagId) {
        return postRepository.countAllPostsByTagId(tagId);
    }
}
