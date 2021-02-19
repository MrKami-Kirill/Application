package main.service;

import lombok.extern.slf4j.Slf4j;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.*;
import main.model.entity.User;
import main.model.repositories.UserRepository;
import main.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service(value = "userService")
@Slf4j
public class UserService implements UserDetailsService {

    @Value("${user.password.length}")
    private int userPasswordLength;

    @Value("${post.announce.max_length}")
    private int announceLength;

    private Map<String, Integer> sessionMap = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CaptchaCodeService captchaCodeService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GlobalSettingService globalSettingService;

    public ResponseEntity<Response> checkAuth(Principal principal) {
        if (principal == null) {
            log.info("Получен ответ на запрос /api/auth/check. Пользователь не авторизован");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.OK);
        }
        String email = principal.getName();
        User currentUser = getUserByEmail(email);
        ResponseEntity<Response> response = new ResponseEntity<>(new AuthUserResponse(currentUser, announceLength), HttpStatus.OK);
        log.info("Получен ответ на запрос /api/auth/check. Пользователь с email '" + email + "' успешно авторизован");
        return response;

    }

    public ResponseEntity<Response> register(RegisterRequest registerRequest) {

        if (!globalSettingService.getGlobalSettingValue("MULTIUSER_MODE")) {
            log.warn("Регистрация нового пользователя невозможна, т.к. глобаальная настройка сайта MULTIUSER_MODE включена");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String email = registerRequest.getEmail();
        String name = registerRequest.getName();
        String password = registerRequest.getPassword();
        String captcha = registerRequest.getCaptcha();
        String captchaSecret = registerRequest.getCaptchaSecret();

        boolean isEmailExist = userRepository.isUserExistByEmail(email.toLowerCase()) > 0;
        boolean isNameValid = name != null && !name.equals("") && !name.isBlank();
        boolean isPasswordLengthValid = password.length() >= userPasswordLength;
        boolean isCaptchaValid = captchaCodeService.isCaptchaValid(captcha, captchaSecret);
        if (!isEmailExist && isNameValid && isPasswordLengthValid && isCaptchaValid) {
            String passwordEncode = new BCryptPasswordEncoder(12).encode(password);
            User user = new User(0, LocalDateTime.now(), name, email, passwordEncode);
            userRepository.save(user);
            log.info("Пользователь '" + email + "' c ID=" + user.getId() + " успешно зарегистрирован на сайте");
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
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<Response> login(LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(auth);
            User user = getUserByEmail(email);
            sessionMap.put(session.getId(), user.getId());
            int moderationCount = 0;
            if (user.getIsModerator() == 1) {
                moderationCount = postService.countAllPostsForModeration();
                log.info("Получено общее кол-во постов на сайте (" + moderationCount + "), требующих проверки модератором");
            }
            ResponseEntity<Response> response = new ResponseEntity<>(new LoginResponse(user, moderationCount), HttpStatus.OK);
            log.info("Пользователь с ID=" + user.getId() + " успешно вошел на сайт. ID сессии: " + session.getId());
            return response;

        } catch (Exception ex) {
            log.warn("Ошибка! Логин и/или пароль введен(ы) неверно");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.OK);
        }
    }

    public ResponseEntity<Response> logout(Principal principal, HttpSession session) {
        String sessionId = session.getId();
        ResponseEntity<Response> response = new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
        if (principal == null) {
            log.info("Получен ответ на запрос /api/auth/check. Пользователь не авторизован");
            return response;
        }
        sessionMap.remove(sessionId);
        session.invalidate();
        log.info("Получен ответ на запрос /api/auth/logout. Пользователь с email '" + principal.getName() + "' успешно вылогинен. Сессия с ID=" + sessionId + " удалена");
        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        return SecurityUser.fromUser(user);
    }

    public Integer getUserIdBySession(HttpSession session) {
        return sessionMap.get(session.getId());
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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Пользователь с email: '" + email + "' не найден"));
    }
}
