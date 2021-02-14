package main.rest.controllers;

import lombok.extern.log4j.Log4j2;
import main.rest.api.response.GetInitResponse;
import main.rest.api.response.GetGlobalSettingResponse;
import main.rest.api.response.GetTagResponse;
import main.rest.api.response.Response;
import main.rest.service.GlobalSettingService;
import main.rest.service.PostService;
import main.rest.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/")
@Log4j2
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
    private ResponseEntity<Response> settings() {
        log.info("Отправлен GET запрос на /api/settings");
        return globalSettingService.getGlobalSettings();
    }

    @GetMapping(value = "init")
    private GetInitResponse init() {
        log.info("Отправлен GET запрос на /api/init");
        return getInitResponse;
    }

    @GetMapping(value = "tag", params = {"query"})
    private ResponseEntity<Response> getTags(@RequestParam(value = "query") String query) {
        log.info("Отправлен GET запрос на /api/tag со следующими параметрами: {" +
                "Query: " + query + "}");
        return tagService.getTags(query);
    }

    @GetMapping(value = "tag")

    private ResponseEntity<Response> getAllTags() {
        log.info("Отправлен GET запрос на /api/tag");
        return tagService.getAllTags();
    }

    @GetMapping(value = "calendar", params = {"year"})
    public ResponseEntity<Response> getAllPostsByCalendar(@RequestParam(value = "year") Integer year) {
        log.info("Отправлен GET запрос на /api/calendar со следующими параметрами: {" +
                "Year: " + year + "}");
        return postService.getAllPostsByCalendar(year);
    }
}
