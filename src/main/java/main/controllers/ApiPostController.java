package main.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import main.model.dto.request.PostRequest;
import main.model.dto.request.VoteRequest;
import main.model.dto.response.Response;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@ComponentScan("service")
@Tag(name = "API для постов блога", description = "Обрабатывает все запросы /api/post/*")
public class ApiPostController {
    
    @Autowired
    private PostService postService;

    public ApiPostController() {
    }

    @Operation(
            summary = "Список постов",
            description = "Метод получения постов со всей сопутствующей информацией для главной страницы и подразделов \"Новые\", \"Самые обсуждаемые\", \"Лучшие\" и \"Старые\""
    )
    @GetMapping(value = "/post", params = {"offset", "limit", "mode"})
    public ResponseEntity<Response> getAllPosts(@RequestParam(value = "offset", defaultValue = "0") @PositiveOrZero(message = "Сдвиг для отображения постов меньше 0") Integer offset,
                                                @RequestParam(value = "limit", defaultValue = "10") @Min(value = 1, message = "Лимит для отображения постов меньше 1") Integer limit,
                                                @RequestParam(value = "mode") @NotNull(message = "Режим для отображения постов не может быть null") @NotBlank(message = "Режим для отображения постов не задан") String mode) {
        log.info("Отправлен GET запрос на /api/post со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Mode: " + mode + "}");
        return postService.getAllPosts(offset, limit, mode);
    }

    @Operation(
            summary = "Поиск постов",
            description = "Метод возвращает посты, соответствующие поисковому запросу - строке \"query\""
    )
    @GetMapping(value = "/post/search", params = {"query", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByQuery(@RequestParam(value = "query") String query,
                                                       @RequestParam(value = "offset", defaultValue = "0") @PositiveOrZero(message = "Сдвиг для отображения постов меньше 0") Integer offset,
                                                       @RequestParam(value = "limit", defaultValue = "10") @Min(value = 1, message = "Лимит для отображения постов меньше 1") Integer limit) {
        log.info("Отправлен GET запрос на /api/search со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Query: " + query + "}");
        return postService.getAllPostsByQuery(query, offset, limit);
    }

    @Operation(
            summary = "Список постов за указанную дату",
            description = "Метод выводит посты за указанную дату, переданную в запросе в параметре \"date\""
    )
    @GetMapping(value = "/post/byDate", params = {"date", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByDate(@RequestParam(value = "date") @NotNull(message = "Дата не может быть null") @NotBlank(message = "Дата не задана") String date,
                                                      @RequestParam(value = "offset", defaultValue = "0") @PositiveOrZero(message = "Сдвиг для отображения постов меньше 0") Integer offset,
                                                      @RequestParam(value = "limit", defaultValue = "10") @Min(value = 1, message = "Лимит для отображения постов меньше 1") Integer limit) {
        log.info("Отправлен GET запрос на /api/byDate со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Date: " + date + "}");
        return postService.getAllPostsByDate(date, offset, limit);
    }

    @Operation(
            summary = "Список постов по тэгу",
            description = "Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра \"tag\""
    )
    @GetMapping(value = "/post/byTag", params = {"tag", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByTag(@RequestParam(value = "tag") @NotNull(message = "Тег не может быть null") @NotBlank(message = "Тег не задан") String tag,
                                                     @RequestParam(value = "offset", defaultValue = "0") @PositiveOrZero(message = "Сдвиг для отображения постов меньше 0") Integer offset,
                                                     @RequestParam(value = "limit", defaultValue = "10") @Min(value = 1, message = "Лимит для отображения постов меньше 1") Integer limit)  {
        log.info("Отправлен GET запрос на /api/byTag со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Tag: " + tag + "}");
        return postService.getAllPostsByTag(tag, offset, limit);
    }

    @Operation(
            summary = "Получение поста",
            description = "Метод выводит данные конкретного поста для отображения на странице поста, в том числе, список комментариев и тэгов, привязанных к данному посту"
    )
    @GetMapping(value = "/post/{id}")
    public ResponseEntity<Response> getPostById(@PathVariable Integer id, HttpServletRequest request) throws Exception {
        log.info("Отправлен GET запрос на /api/{id} со следующими параметрами: {" +
                "Id:" + id + "," +
                "SessionId:" + request.getSession().getId() + "}");
        return postService.getPostById(id, request.getSession());
    }

    @Operation(
            summary = "Список постов текущего пользователя",
            description = "Метод выводит только те посты, которые создал текущий пользователь"
    )
    @GetMapping(value = "/post/my", params = {"status", "offset", "limit"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> getMyPosts(@RequestParam(value = "status") @NotNull(message = "Статус постов для отображение не может быть null") @NotBlank(message = "Статус постов для отображение не задан") String status,
                                               @RequestParam(value = "offset", defaultValue = "0") @PositiveOrZero(message = "Сдвиг для отображения постов меньше 0") Integer offset,
                                               @RequestParam(value = "limit", defaultValue = "10") @Min(value = 1, message = "Лимит для отображения постов меньше 1") Integer limit,
                                                HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/post/my со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Status: " + status + "}");
        return postService.getMyPosts(status, offset, limit, request.getSession());
    }

    @Operation(
            summary = "Список постов на модерацию",
            description = "Метод выводит все посты, которые требуют модерационных действий"
    )
    @GetMapping(value = "/post/moderation", params = {"status", "offset", "limit"})
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> getAllModeratePosts(
                                               @RequestParam(value = "status") @NotNull(message = "Статус для отображения постов не может быть null") @NotBlank(message = "Статус для отображения постов не задан") String status,
                                               @RequestParam(value = "offset", defaultValue = "0") @PositiveOrZero(message = "Сдвиг для отображения постов меньше 0") Integer offset,
                                               @RequestParam(value = "limit", defaultValue = "10") @Min(value = 1, message = "Лимит для отображения постов меньше 1") Integer limit,
                                               HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/post/moderation со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + "}");
        return postService.getAllModeratePosts(status, offset, limit, request.getSession());
    }
    @Operation(
            summary = "Добавление поста",
            description = "Метод отправляет данные поста, которые пользователь ввёл в форму публикации"
    )
    @PostMapping(value = "/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> addPost(@RequestBody PostRequest postRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/post со следующими параметрами: {" +
                "TimeStamp: " + postRequest.getTimestamp() + ", " +
                "Active: " + postRequest.getActive() + ", " +
                "Title: " + postRequest.getActive() + ", " +
                "Text: " + postRequest.getActive() + ", " +
                "Tags: " + Arrays.toString(postRequest.getTags().toArray()) + "}");
        return postService.addPost(postRequest, request.getSession());
    }

    @Operation(
            summary = "Редактирование поста",
            description = "Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму публикации"
    )
    @PutMapping(value = "/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> editPost(@PathVariable Integer id, @RequestBody PostRequest postRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/post/{ID} со следующими параметрами: {" +
                "TimeStamp: " + postRequest.getTimestamp() + ", " +
                "Active: " + postRequest.getActive() + ", " +
                "Title: " + postRequest.getActive() + ", " +
                "Text: " + postRequest.getActive() + ", " +
                "Tags: " + Arrays.toString(postRequest.getTags().toArray()) + "}");
        return postService.editPost(id, postRequest, request.getSession());
    }

    @Operation(
            summary = "Лайк поста",
            description = "Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя"
    )
    @PostMapping(value = "/post/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> likePost(@RequestBody VoteRequest voteRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/post/like со следующими параметрами: {" +
                "Post_id: " + voteRequest.getPostId() + "}");
        byte value = 1;
        return postService.vote(voteRequest, request.getSession(), value);
    }

    @Operation(
            summary = "Дизлайк поста",
            description = "Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя"
    )
    @PostMapping(value = "/post/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> dislikePost(@RequestBody VoteRequest voteRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/post/dislike со следующими параметрами: {" +
                "Post_id: " + voteRequest.getPostId() + "}");
        byte value = -1;
        return postService.vote(voteRequest, request.getSession(), value);
    }

}
