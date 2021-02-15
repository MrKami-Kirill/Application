package main.controllers;

import lombok.extern.log4j.Log4j2;
import main.api.response.Response;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/")
@Log4j2
public class ApiPostController {

    @Autowired
    private PostService postService;

    public ApiPostController() {
    }

    @GetMapping(value = "post", params = {"offset", "limit", "mode"})
    public ResponseEntity<Response> getAllPosts(@RequestParam(value = "offset") int offset,
                                                @RequestParam(value = "limit") int limit,
                                                @RequestParam(value = "mode") String mode) {
        log.info("Отправлен GET запрос на /api/post со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Mode: " + mode + "}");
        return postService.getAllPosts(offset, limit, mode);
    }

    @GetMapping(value = "post/search", params = {"query", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByQuery(@RequestParam(value = "query") String query,
                                                       @RequestParam(value = "offset") int offset,
                                                       @RequestParam(value = "limit") int limit) {
        log.info("Отправлен GET запрос на /api/search со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Query: " + query + "}");
        return postService.getAllPostsByQuery(query, offset, limit);
    }

    @GetMapping(value = "post/byDate", params = {"date", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByDate(@RequestParam(value = "date") String date,
                                                      @RequestParam(value = "offset") int offset,
                                                      @RequestParam(value = "limit") int limit) {
        log.info("Отправлен GET запрос на /api/byDate со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Date: " + date + "}");
        return postService.getAllPostsByDate(date, offset, limit);
    }

    @GetMapping(value = "post/byTag", params = {"tag", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByTag(@RequestParam(value = "tag") String tag,
                                                      @RequestParam(value = "offset") int offset,
                                                      @RequestParam(value = "limit") int limit) {
        log.info("Отправлен GET запрос на /api/byTag со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Tag: " + tag + "}");
        return postService.getAllPostsByTag(tag, offset, limit);
    }

    @GetMapping(value = "post/{id}")
    public ResponseEntity<Response> getAllPostsByTag(@PathVariable Integer id, HttpServletRequest request) {
        log.info("Отправлен GET запрос на /api/{id} со следующими параметрами: {" +
                "Id:" + id + "," +
                "SessionId:" + request.getSession().getId() + "}");
        return postService.getPostById(id, request.getSession());
    }

    @GetMapping(value = "post/my", params = {"status", "offset", "limit"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> getMyPosts(@RequestParam(value = "status") String status,
                                                @RequestParam(value = "offset") int offset,
                                                @RequestParam(value = "limit") int limit,
                                                HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/post/my со следующими параметрами: {" +
                "Offset: " + offset + ", " +
                "Limit: " + limit + ", " +
                "Status: " + status + "}");
        return postService.getMyPosts(status, offset, limit, request.getSession());
    }

}
