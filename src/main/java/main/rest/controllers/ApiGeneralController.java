package main.rest.controllers;

import main.rest.api.response.GetInitResponse;
import main.rest.api.response.GetGlobalSettingResponse;
import main.rest.api.response.GetTagResponse;
import main.rest.api.response.Response;
import main.rest.service.GlobalSettingService;
import main.rest.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/")
public class ApiGeneralController {

    @Autowired
    private GetInitResponse getInitResponse;
    @Autowired
    private GlobalSettingService globalSettingService;
    @Autowired
    private TagService tagService;

    public ApiGeneralController() {
    }

    @GetMapping(value = "settings")
    private ResponseEntity<Response> settings() {
        return globalSettingService.getGlobalSettings();
    }

    @GetMapping(value = "init")
    private GetInitResponse init() {
        return getInitResponse;
    }

    @GetMapping(value = "tag", params = {"query"})
    private ResponseEntity<Response> getTags(@RequestParam(value = "query") String query) {
        return tagService.getTags(query);
    }
}
