package main.rest.service;

import lombok.extern.log4j.Log4j2;
import main.rest.api.request.PostRegisterRequest;
import main.rest.api.request.Request;
import main.rest.api.response.*;
import main.rest.model.entity.CaptchaCode;
import main.rest.model.entity.User;
import main.rest.model.repositories.CaptchaCodeRepository;
import main.rest.model.repositories.PostRepository;
import main.rest.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class UserService {

    @Value("${user.password.length}")
    private int userPasswordLength;

    //Пока как заглушка. При реализации авторизации (api/auth/login) будем заполнять (put)
    private Map<String, Integer> sessionId = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<Response> checkAuth(HttpSession session) {
        if (!sessionId.containsKey(session.getId())) {
            log.warn("Ошибка! Сессия отсутствует!");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.OK);
        } else {
            User user = getUserBySession(session);
            return getResponseEntityByUserExist(user);
        }
    }

    private ResponseEntity<Response> getResponseEntityByUserExist(User user) {
        if (user != null) {
            log.info("Для сессии найден пользователь " + user.getName());
            return new ResponseEntity<>(new AuthUserResponse(user, postRepository.countAllPostsForModeration()),
                    HttpStatus.OK);
        } else {
            log.warn("Ошибка! Сессия отсутствует/ пользователь не авторизован");
            return new ResponseEntity<>(new BadRequestMessageResponse("Пользователь не авторизован"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public Integer getUserIdBySession(HttpSession session) {
        return sessionId.get(session.getId());
    }

    public User getUserBySession(HttpSession session) {
        Integer userId = getUserIdBySession(session);
        if (userId == null) {
            return null;
        }
        return getUser(userId).getBody();
    }

    public ResponseEntity<User> getUser(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    public ResponseEntity<Response> register(PostRegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String name = registerRequest.getName();
        String password = registerRequest.getPassword();
        String captcha = registerRequest.getCaptcha();
        String captchaSecret = registerRequest.getCaptchaSecret();

        boolean isEmailExist = userRepository.isUserExistByEmail(email.toLowerCase()) > 0;
        boolean isNameValid = name != null && !name.equals("") && !name.isBlank();
        boolean isPasswordLengthValid = password.length() >= userPasswordLength;
        boolean isCaptchaValid = isCaptchaValid(captcha, captchaSecret);
        if (!isEmailExist && isNameValid && isPasswordLengthValid && isCaptchaValid) {
            String passwordEncode = bCryptPasswordEncoder.encode(password);
            log.info("Пароль пользователя успешно закодирован с помощью BCryptPasswordEncoder");
            User user = new User(false, LocalDateTime.now(), name, email, passwordEncode);
            userRepository.save(user);
            log.info("Пользователь '" + name + "' c id=" + user.getId() + " успешно зарегистрирован на сайте");
            return new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
        } else {
            HashMap<String, String> errors = new HashMap<>();
            if (isEmailExist) {
                log.warn("Пользователь с email '" + email +  "' уже зарегистрирован");
                errors.put("email", "Этот e-mail уже зарегистрирован");
            }
            if (!isNameValid) {
                log.warn("Имя пользователя не может быть пустым или содержать только пробелы");
                errors.put("name", "Имя указано неверно");
            }
            if (!isPasswordLengthValid) {
                log.warn("Пароль '" + password + "' содержит менее 6 символов");
                errors.put("password", "Пароль короче 6-ти символов");
            }
            if (!isCaptchaValid) {
                log.warn("Введенной значение каптчи '" + captcha + "' не совпало с картинкой");
                errors.put("captcha", "Код с картинки введён неверно");
            }
            return new ResponseEntity<>(new BadRequestMessageForRegistrationResponse(errors), HttpStatus.BAD_REQUEST);
        }

    }

    private boolean isCaptchaValid(String captcha, String captchaSecret) {
        CaptchaCode captchaCode = captchaCodeRepository.getCaptchaBySecretCode(captchaSecret);
        return captchaCode.getCode().equals(captcha);
    }
}
