package main.rest.controllers;

import main.rest.api.response.Response;
import main.rest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/")
public class ApiPostController {

    @Autowired
    private PostService postService;

    public ApiPostController() {
    }

    @GetMapping(value = "post", params = {"offset", "limit", "mode"})
    public ResponseEntity<Response> getAllPostsWithParams(@RequestParam(value = "offset") int offset,
                                        @RequestParam(value = "limit") int limit,
                                        @RequestParam(value = "mode") String mode) {
        return postService.getPostsWithParams(offset, limit, mode);
    }


}
