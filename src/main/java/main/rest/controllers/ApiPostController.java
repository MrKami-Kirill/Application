package main.rest.controllers;

import main.rest.api.response.Response;
import main.rest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/")
public class ApiPostController {

    @Autowired
    private PostService postService;

    public ApiPostController() {
    }

    @GetMapping(value = "post", params = {"offset", "limit", "mode"})
    public ResponseEntity<Response> getAllPosts(@RequestParam(value = "offset") int offset,
                                                @RequestParam(value = "limit") int limit,
                                                @RequestParam(value = "mode") String mode) {
        return postService.getAllPosts(offset, limit, mode);
    }

    @GetMapping(value = "post/search", params = {"query", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByQuery(@RequestParam(value = "query") String query,
                                                       @RequestParam(value = "offset") int offset,
                                                       @RequestParam(value = "limit") int limit) {
        return postService.getAllPostsByQuery(query, offset, limit);
    }

    @GetMapping(value = "post/byDate", params = {"date", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByDate(@RequestParam(value = "date") String date,
                                                      @RequestParam(value = "offset") int offset,
                                                      @RequestParam(value = "limit") int limit) {
        return postService.getAllPostsByDate(date, offset, limit);
    }

    @GetMapping(value = "post/byTag", params = {"tag", "offset", "limit"})
    public ResponseEntity<Response> getAllPostsByTag(@RequestParam(value = "tag") String tag,
                                                      @RequestParam(value = "offset") int offset,
                                                      @RequestParam(value = "limit") int limit) {
        return postService.getAllPostsByTag(tag, offset, limit);
    }

    @GetMapping(value = "post/{id}")
    public ResponseEntity<Response> getAllPostsByTag(@PathVariable Integer id, HttpServletRequest request) {
        return postService.getPostById(id, request.getSession());
    }

}
