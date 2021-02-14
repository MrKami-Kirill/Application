package main.rest.controllers;


import lombok.extern.log4j.Log4j2;
import main.rest.api.request.PostRegisterRequest;
import main.rest.api.response.Response;
import main.rest.service.CaptchaCodeService;
import main.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/auth/")
@Log4j2
public class ApiAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaCodeService captchaCodeService;

    public ApiAuthController() {
    }


    @GetMapping(value = "check")
    private ResponseEntity<Response> checkAuth(HttpServletRequest request) {
        log.info("Отправлен GET запрос на /api/auth/check");
        return userService.checkAuth(request.getSession());
    }

    @GetMapping(value = "captcha")
    private ResponseEntity<Response> getCaptcha() {
        log.info("Отправлен GET запрос на /api/auth/captcha");
        return captchaCodeService.getCaptcha();
    }

    @PostMapping(value = "register")
    private ResponseEntity<Response> register(@RequestBody PostRegisterRequest registerRequest) {
        log.info("Отправлен POST запрос на /api/auth/register со следующими параметрами: {" +
                "Email: " + registerRequest.getEmail() + ", " +
                "Name: " + registerRequest.getName() + ", " +
                "Password: " + registerRequest.getPassword() + ", " +
                "Captcha: " + registerRequest.getCaptcha() + ", " +
                "CaptchaSecret: " + registerRequest.getCaptchaSecret() + "}");
        return userService.register(registerRequest);

    }
}

