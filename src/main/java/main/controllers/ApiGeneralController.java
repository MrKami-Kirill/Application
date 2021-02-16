package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.api.response.GetInitResponse;
import main.api.response.Response;
import main.service.GlobalSettingService;
import main.service.PostService;
import main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/")
@Slf4j
public class ApiGeneralController {
    
    @Autowired
    private GetInitResponse getInitResponse;
    @Autowired
    private GlobalSettingService globalSettingService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PostService postService;

    public ApiGeneralController() {
    }

    @GetMapping(value = "settings")
    public ResponseEntity<Response> settings() {
        log.info("Отправлен GET запрос на /api/settings");
        return globalSettingService.getGlobalSettingsResponse();
    }

    @GetMapping(value = "init")
    public GetInitResponse init() {
        log.info("Отправлен GET запрос на /api/init");
        return getInitResponse;
    }

    @GetMapping(value = "tag", params = {"query"})
    public ResponseEntity<Response> getTags(@RequestParam(value = "query") String query) {
        log.info("Отправлен GET запрос на /api/tag со следующими параметрами: {" +
                "Query: " + query + "}");
        return tagService.getTags(query);
    }

    @GetMapping(value = "tag")

    public ResponseEntity<Response> getAllTags() {
        log.info("Отправлен GET запрос на /api/tag");
        return tagService.getAllTags();
    }

    @GetMapping(value = "calendar", params = {"year"})
    public ResponseEntity<Response> getAllPostsByCalendar(@RequestParam(value = "year") Integer year) {
        log.info("Отправлен GET запрос на /api/calendar со следующими параметрами: {" +
                "Year: " + year + "}");
        return postService.getAllPostsByCalendar(year);
    }

    @PostMapping(value = "image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> uploadImage() {
        return null;
    }
}
