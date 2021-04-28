package main.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import main.model.dto.request.ChangePasswordRequest;
import main.model.dto.request.LoginRequest;
import main.model.dto.request.RegisterRequest;
import main.model.dto.request.RestorePasswordRequest;
import main.model.dto.response.Response;
import main.service.CaptchaCodeService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/auth")
@Slf4j
@ComponentScan("service")
@Tag(name = "API для авторизации", description = "Обрабатывает все запросы /api/auth/*")
public class ApiAuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaCodeService captchaCodeService;


    @Operation(
            summary = "Статус авторизации",
            description = "Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован"
    )
    @GetMapping(value = "/check")
    public ResponseEntity<Response> checkAuth(Principal principal
    ) {
        log.info("Отправлен GET запрос на /api/auth/check");
        return userService.checkAuth(principal);
    }

    @Operation(
            summary = "Запрос каптчи",
            description = "Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных"
    )
    @GetMapping(value = "/captcha")
    public ResponseEntity<Response> getCaptcha() {
        log.info("Отправлен GET запрос на /api/auth/captcha");
        return captchaCodeService.getCaptcha();
    }

    @Operation(
            summary = "Регистрация",
            description = "Метод создаёт пользователя в базе данных, если введённые данные верны"
    )
    @PostMapping(value = "/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest registerRequest) {
        log.info("Отправлен POST запрос на /api/auth/register со следующими параметрами: {" +
                "Email: " + registerRequest.getEmail() + ", " +
                "Name: " + registerRequest.getName() + ", " +
                "Password: " + registerRequest.getPassword() + ", " +
                "Captcha: " + registerRequest.getCaptcha() + ", " +
                "CaptchaSecret: " + registerRequest.getCaptchaSecret() + "}");
        return userService.register(registerRequest);

    }

    @Operation(
            summary = "Вход",
            description = "Метод проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны"
    )
    @PostMapping(value = "/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/auth/login со следующими параметрами: {" +
                "Email: " + loginRequest.getEmail() + ", " +
                "Password: " + loginRequest.getPassword() + "}");
        return userService.login(loginRequest, request.getSession());
    }

    @Operation(
            summary = "Выход",
            description = "Метод разлогинивает пользователя: удаляет идентификатор его сессии из списка авторизованных"
    )
    @GetMapping(value = "/logout")
    public ResponseEntity<Response> logout(Principal principal, HttpServletRequest request) {
        log.info("Отправлен POST запрос на /api/auth/logout с ID сессии: " + request.getSession().getId());
        return userService.logout(principal, request.getSession());
    }

    @Operation(
            summary = "Восстановление пароля",
            description = "Метод проверяет наличие в базе пользователя с указанным e-mail. Если пользователь найден, ему должно отправляться письмо со ссылкой на восстановление пароля"
    )
    @PostMapping(value = "/restore")
    public ResponseEntity<Response> restorePassword(@RequestBody RestorePasswordRequest passwordRequest) {
        log.info("Отправлен POST запрос на /api/auth/restore со следующими параметрами: {" +
                "Email: " + passwordRequest.getEmail()  + "}");
        return userService.restorePassword(passwordRequest);
    }

    @Operation(
            summary = "Изменение пароля",
            description = "Метод проверяет корректность кода восстановления пароля и корректность кодов капчи"
    )
    @PostMapping(value = "/password")
    public ResponseEntity<Response> changePassword(@RequestBody ChangePasswordRequest passwordRequest) throws Exception {
        log.info("Отправлен POST запрос на /api/auth/password со следующими параметрами: {" +
                "Code: " + passwordRequest.getCode()  + ", " +
                "Password: " + passwordRequest.getPassword()  + ", " +
                "Captcha: " + passwordRequest.getCaptcha()  + ", " +
                "Captcha_secret: " + passwordRequest.getCaptchaSecret()  + "}");
        return userService.changePassword(passwordRequest);
    }
}

