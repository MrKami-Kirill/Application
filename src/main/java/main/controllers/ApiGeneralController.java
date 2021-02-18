package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.api.request.PostCommentRequest;
import main.api.request.PostModerationRequest;
import main.api.response.InitResponse;
import main.api.response.Response;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class ApiGeneralController {
    
    @Autowired
    private InitResponse initResponse;
    @Autowired
    private GlobalSettingService globalSettingService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PostService postService;
    @Autowired
    private FileService uploadFileService;
    @Autowired
    private PostCommentService postCommentService;

    public ApiGeneralController() {
    }

    @GetMapping(value = "/settings")
    public ResponseEntity<Response> settings() {
        log.info("Отправлен GET запрос на /api/settings");
        return globalSettingService.getGlobalSettingsResponse();
    }

    @GetMapping(value = "/init")
    public InitResponse init() {
        log.info("Отправлен GET запрос на /api/init");
        return initResponse;
    }

    @GetMapping(value = "/tag", params = {"query"})
    public ResponseEntity<Response> getTags(@RequestParam(value = "query") String query) {
        log.info("Отправлен GET запрос на /api/tag со следующими параметрами: {" +
                "Query: " + query + "}");
        return tagService.getTags(query);
    }

    @GetMapping(value = "/tag")
    public ResponseEntity<Response> getAllTags() {
        log.info("Отправлен GET запрос на /api/tag");
        return tagService.getAllTags();
    }

    @GetMapping(value = "/calendar", params = {"year"})
    public ResponseEntity<Response> getAllPostsByCalendar(@RequestParam(value = "year") Integer year) {
        log.info("Отправлен GET запрос на /api/calendar со следующими параметрами: {" +
                "Year: " + year + "}");
        return postService.getAllPostsByCalendar(year);
    }

    @PostMapping(value = "/image", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> uploadImage(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/image");
        return uploadFileService.uploadFile(file, request.getSession());
    }

    @PostMapping(value = "/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> addComment(@RequestBody PostCommentRequest commentRequest, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/comment");
        return postCommentService.addComment(commentRequest, request.getSession());
    }

    @PostMapping(value = "/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> moderation(@RequestBody PostModerationRequest moderationRequest, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/moderation");
        return postService.moderation(moderationRequest, request.getSession());
    }
}
