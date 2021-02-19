package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.api.request.PostRequest;
import main.api.response.Response;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@Validated
public class ApiPostController {
    
    @Autowired
    private PostService postService;

    public ApiPostController() {
    }

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

    @GetMapping(value = "/post/{id}")
    public ResponseEntity<Response> getAllPostsByTag(@PathVariable Integer id, HttpServletRequest request) throws Exception {
        log.info("Отправлен GET запрос на /api/{id} со следующими параметрами: {" +
                "Id:" + id + "," +
                "SessionId:" + request.getSession().getId() + "}");
        return postService.getPostById(id, request.getSession());
    }

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

}
