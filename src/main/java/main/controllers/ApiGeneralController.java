package main.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import main.model.dto.request.*;
import main.config.InitConfig;
import main.model.dto.response.Response;
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
@Tag(name = "Прочие API", description = "Обрабатывает прочие запросы")
public class ApiGeneralController {

    @Autowired
    private InitConfig initConfig;
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

    @Operation(
            summary = "Получение настроек",
            description = "Метод возвращает глобальные настройки блога из таблицы \"global_settings\""
    )
    @GetMapping(value = "/settings")
    public ResponseEntity<Response> settings() {
        log.info("Отправлен GET запрос на /api/settings");
        return globalSettingService.getGlobalSettingsResponse();
    }

    @Operation(
            summary = "Общие данные блога",
            description = "Метод возвращает общую информацию о блоге"
    )
    @GetMapping(value = "/init")
    public InitConfig init() {
        log.info("Отправлен GET запрос на /api/init");
        return new InitConfig(
                initConfig.getTitle(),
                initConfig.getSubtitle(),
                initConfig.getPhone(),
                initConfig.getEmail(),
                initConfig.getCopyright(),
                initConfig.getCopyrightFrom());
    }

    @Operation(
            summary = "Получение списка тэгов",
            description = "Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре \"query\""
    )
    @GetMapping(value = "/tag", params = {"query"})
    public ResponseEntity<Response> getTags(@RequestParam(value = "query") String query) {
        log.info("Отправлен GET запрос на /api/tag со следующими параметрами: {" +
                "Query: " + query + "}");
        return tagService.getTags(query);
    }

    @Operation(
            summary = "Получение всех тегов",
            description = "Метод выдаёт список всех тэгов"
    )
    @GetMapping(value = "/tag")
    public ResponseEntity<Response> getAllTags() {
        log.info("Отправлен GET запрос на /api/tag");
        return tagService.getAllTags();
    }

    @Operation(
            summary = "Календарь (количества публикаций)",
            description = "Метод выводит количества публикаций на каждую дату переданного в параметре \"year\" года или текущего года, если параметр \"year\" не задан"
    )
    @GetMapping(value = "/calendar", params = {"year"})
    public ResponseEntity<Response> getAllPostsByCalendar(@RequestParam(value = "year") Integer year) {
        log.info("Отправлен GET запрос на /api/calendar со следующими параметрами: {" +
                "Year: " + year + "}");
        return postService.getAllPostsByCalendar(year);
    }

    @Operation(
            summary = "Загрузка изображений",
            description = "Метод загружает на сервер изображение в папку upload и три случайные подпапки"
    )
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/image");
        return postService.uploadImage(file, request.getSession());
    }

    @Operation(
            summary = "Отправка комментария к посту",
            description = "Метод добавляет комментарий к посту"
    )
    @PostMapping(value = "/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> addComment(@RequestBody PostCommentRequest commentRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/comment");
        return postCommentService.addComment(commentRequest, request.getSession());
    }

    @Operation(
            summary = "Модерация поста",
            description = "Метод фиксирует действие модератора по посту: его утверждение или отклонение"
    )
    @PostMapping(value = "/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> moderation(@RequestBody PostModerationRequest moderationRequest, HttpServletRequest request) throws Exception {
        log.info("Отправлен POST запрос на /api/moderation");
        return postService.moderation(moderationRequest, request.getSession());
    }

    @Operation(
            summary = "Редактирование моего профиля",
            description = "Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля"
    )
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

    @Operation(
            summary = "Редактирование моего профиля",
            description = "Метод обрабатывает информацию, введённую пользователем в форму редактирования своего профиля"
    )
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

    @Operation(
            summary = "Статистика текущего пользователя",
            description = "Метод возвращает статистику постов текущего авторизованного пользователя"
    )
    @GetMapping(value = "/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Response> getMyStatistics(HttpServletRequest request) {
        return postService.getMyStatistics(request.getSession());
    }

    @Operation(
            summary = "Статистика по всему блогу",
            description = "Метод выдаёт статистику по всем постам блога"
    )
    @GetMapping(value = "/statistics/all")
    public ResponseEntity<Response> getAllStatistics() {
        return postService.getAllStatistics();
    }

    @Operation(
            summary = "Сохранение настроек",
            description = "Метод записывает глобальные настройки блога в таблицу \"global_settings\", если запрашивающий пользователь авторизован и является модератором"
    )
    @PutMapping(value = "/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Response> setGlobalSettings(@RequestBody GlobalSettingRequest settingRequest, HttpServletRequest request) throws Exception {
        return globalSettingService.setGlobalSettnigs(settingRequest, request.getSession());
    }
}
