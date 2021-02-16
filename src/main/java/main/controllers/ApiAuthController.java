package main.controllers;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import main.api.request.PostLoginRequest;
import main.api.request.PostRegisterRequest;
import main.api.response.Response;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/auth/")
@Data
@Slf4j
public class ApiAuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaCodeService captchaCodeService;


    @GetMapping(value = "check")
    private ResponseEntity<Response> checkAuth(Principal principal
    ) {
        log.info("Отправлен GET запрос на /api/auth/check");
        return userService.checkAuth(principal);
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

    @PostMapping(value = "login")
    private ResponseEntity<Response> login(@RequestBody PostLoginRequest loginRequest, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/auth/login со следующими параметрами: {" +
                "Email: " + loginRequest.getEmail() + ", " +
                "Password: " + loginRequest.getPassword() + "}");
        return userService.login(loginRequest, request.getSession());
    }

    @GetMapping(value = "logout")
    private ResponseEntity<Response> logout(Principal principal, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/auth/logout с ID сессии: " + request.getSession().getId());
        return userService.logout(principal, request.getSession());
    }
}

