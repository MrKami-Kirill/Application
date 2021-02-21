package main.controllers;

import lombok.extern.slf4j.Slf4j;
import main.api.request.*;
import main.api.response.InitResponse;
import main.api.response.Response;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@ComponentScan("service")
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
    private PostCommentService postCommentService;
    @Autowired
    private UserService userService;

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

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/image");
        return postService.uploadImage(file, request.getSession());
    }

    @PostMapping(value = "/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> addComment(@RequestBody PostCommentRequest commentRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/comment");
        return postCommentService.addComment(commentRequest, request.getSession());
    }

    @PostMapping(value = "/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> moderation(@RequestBody PostModerationRequest moderationRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/moderation");
        return postService.moderation(moderationRequest, request.getSession());
    }

    @PostMapping(value = "/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> editProfile(@RequestBody EditProfileRequest editProfileRequest,
                                                   Principal principal) throws Exception {
        log.info("Отправлен POST запрос на /api/profile/my со следующими параметрами: {" +
                "Email:" + editProfileRequest.getEmail() + "," +
                "Name:" + editProfileRequest.getName() + "," +
                "Password:" + editProfileRequest.getPassword() + "," +
                "RemovePhoto:" + editProfileRequest.getRemovePhoto()
                + "}");
        return userService.editProfile(editProfileRequest, principal);
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> editProfileWithPhoto(@ModelAttribute EditProfileWithPhotoRequest editProfileRequest,
                                                         Principal principal) throws Exception {
        log.info("Отправлен POST запрос на /api/profile/my со следующими параметрами: {" +
                "Email:" + editProfileRequest.getEmail() + "," +
                "Name:" + editProfileRequest.getName() + "," +
                "Password:" + editProfileRequest.getPassword() + "," +
                "RemovePhoto:" + editProfileRequest.getRemovePhoto() + "," +
                "PhotoFileName:" + editProfileRequest.getPhoto().getOriginalFilename() + "," +
                "PhotoFileSize:" + editProfileRequest.getPhoto().getSize() + "}");
        return userService.editProfile(editProfileRequest, principal);
    }

    @GetMapping(value = "/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> getMyStatistics(HttpServletRequest request) throws Exception {
        return postService.getMyStatistics(request.getSession());
    }

    @GetMapping(value = "/statistics/all")
    public ResponseEntity<Response> getAllStatistics() {
        return postService.getAllStatistics();
    }

    @PutMapping(value = "/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> setGlobalSettings(@RequestBody GlobalSettingRequest settingRequest, HttpServletRequest request) throws Exception {
        return globalSettingService.setGlobalSettnigs(settingRequest, request.getSession());
    }
}
