package main.service;

import lombok.extern.slf4j.Slf4j;
import main.model.dto.request.*;
import main.model.dto.response.*;
import main.model.entity.User;
import main.repositories.UserRepository;
import main.model.security.SecurityUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${user.image.max_size}")
    private int maxPhotoSize;

    @Value("${user.image.avatar_dir}")
    private String avatarDir;

    @Value("${user.image.format}")
    private String format;

    @Value("${user.password.restore.code.length}")
    private int codeLength;

    @Value("${user.password.restore.message.from}")
    private String messageFrom;

    @Value("${user.password.restore.message.subject}")
    private String messageSubject;

    @Value("${user.password.restore.message.link}")
    private String messageLink;

    @Value("${user.password.restore.message.server_link}")
    private String messageServerLink;

    @Value("${server.port}")
    private String serverPort;

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

    @Autowired
    private FileService fileService;

    @Autowired
    private JavaMailSender mailSender;

    public ResponseEntity<Response> checkAuth(Principal principal) {
        if (principal == null) {
            log.info("Получен ответ на запрос /api/auth/check. Пользователь не авторизован");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.OK);
        }
        String email = principal.getName();
        User currentUser = getUserByEmail(email);
        ResponseEntity<Response> response = new ResponseEntity<>(new AuthUserResponse(currentUser, announceLength), HttpStatus.OK);
        log.info("Получен ответ на запрос /api/auth/check. Пользователь с email '" + email + "' авторизован");
        return response;

    }

    public ResponseEntity<Response> register(RegisterRequest registerRequest) {

        if (!globalSettingService.getGlobalSettingValue(GlobalSettingService.MULTIUSER_MODE_CODE)) {
            log.warn("Регистрация нового пользователя невозможна, т.к. глобальная настройка сайта MULTIUSER_MODE включена");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String email = registerRequest.getEmail();
        String name = registerRequest.getName();
        String password = registerRequest.getPassword();
        String captcha = registerRequest.getCaptcha();
        String captchaSecret = registerRequest.getCaptchaSecret();

        boolean isEmailExist = userRepository.isUserExistByEmail(email.toLowerCase());
        boolean isNameValid = isStringParamValid(name);
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

    public ResponseEntity<Response> editProfile(EditProfileRequest profileRequest, Principal principal) throws Exception {
        if (principal == null) {
            log.warn("Изменение профиля невозможно, т.к. пользователь не авторизован");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = getUserByEmail(principal.getName());

        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String password = profileRequest.getPassword();
        Integer remotePhoto = profileRequest.getRemovePhoto();

        boolean isEmailExist = userRepository.isUserExistByEmail(email.toLowerCase());
        boolean isEmailValid = isEmailExist && user.getEmail().equalsIgnoreCase(email);
        boolean isNameValid = isStringParamValid(name);
        boolean isPasswordLengthValid;
        boolean isPhotoValid = true;

        if (isStringParamValid(password)) {
            isPasswordLengthValid = password.length() >= userPasswordLength;
            if (isPasswordLengthValid) {
                user.setPassword(new BCryptPasswordEncoder(12).encode(password));
            }
        } else {
            isPasswordLengthValid = true;
        }

        String photoPath = "";
        if (remotePhoto != null && remotePhoto == 1) {
            String currentPhoto = user.getPhoto();
            fileService.deleteFileByPath(currentPhoto);
            user.setPhoto(null);
        } else {
            if (profileRequest instanceof EditProfileWithPhotoRequest) {
                MultipartFile newPhoto = ((EditProfileWithPhotoRequest) profileRequest).getPhoto();
                if (newPhoto == null) {
                    log.warn("Изображение для загрузки на сервер отсутствует");
                    throw new Exception("Изображение для загрузки на сервер отсутствует");
                } else {
                    if (newPhoto.getSize() < 0 || newPhoto.getSize() > maxPhotoSize) {
                        log.warn("Размер изображения превысил максимальное значение (10Мб) или отрицательный");
                        isPhotoValid = false;
                    } else {
                        if (user.getPhoto() != null) {
                            fileService.deleteFileByPath(user.getPhoto());
                        }
                        photoPath = fileService.createFile(avatarDir, format, newPhoto);
                        user.setPhoto(photoPath);
                    }
                }
            }
        }

        HashMap<String, String> errors = new HashMap<>();
        ResponseEntity<Response> response;
        if (!isEmailValid || !isNameValid || !isPasswordLengthValid || !isPhotoValid) {
                if (!isNameValid) {
                    log.warn("Имя пользователя не может быть пустым или содержать только пробелы");
                    errors.put("name", "Имя указано неверно");
                }

                if (!isEmailValid) {
                    log.warn("Пользователь с email '" + email + "' уже зарегистрирован");
                    errors.put("email", "Этот e-mail уже зарегистрирован");
                }

                if (!isPasswordLengthValid) {
                    log.warn("Пароль '" + password + "' содержит менее 6 символов");
                    errors.put("password", "Пароль короче 6-ти символов");
                }
                if (!isPasswordLengthValid) {
                    log.warn("Пароль '" + password + "' содержит менее 6 символов");
                    errors.put("password", "Пароль короче 6-ти символов");
                }
                response = new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
            } else {
                user.setName(name);
                user.setEmail(email);
                userRepository.save(user);
                response = new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
        }
        return response;
    }

    public ResponseEntity<Response> restorePassword(RestorePasswordRequest passwordRequest) {
        String email = passwordRequest.getEmail();
        User user;
        if (userRepository.isUserExistByEmail(email)) {
            user = getUserByEmail(email);
        } else {
            log.warn("Пользователь с email '" + email + "' не найден");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.BAD_REQUEST);
        }

        String code = RandomStringUtils.random(45, true, true);
        user.setCode(code);
        userRepository.save(user);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(messageFrom);
        message.setTo(email);
        message.setSubject(messageSubject);
        String messageText = "Для смены пароля перейдите по следующей ссылке: " + messageServerLink + serverPort + messageLink + code;
        message.setText(messageText);
        mailSender.send(message);
        log.info("Отправлено код '" + code + "' восстановления пароля на email '" + email + "'");
        return new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
    }

    public ResponseEntity<Response> changePassword(ChangePasswordRequest passwordRequest) throws Exception {
        String code = passwordRequest.getCode();
        String password = passwordRequest.getPassword();
        String captcha = passwordRequest.getCaptcha();
        String captchaSecret = passwordRequest.getCaptchaSecret();

        if (isStringParamValid(code) && isStringParamValid(password) && isStringParamValid(captcha) && isStringParamValid(captchaSecret)) {
            HashMap<String, String> errors = new HashMap<>();
            User user = userRepository.findUserByCode(code).orElse(null);
            boolean isCodeValid = user != null;
            boolean isCaptchaValid = captchaCodeService.isCaptchaValid(captcha, captchaSecret);
            boolean isPasswordLengthValid = password.length() >= userPasswordLength;
            if (isCodeValid && isCaptchaValid && isPasswordLengthValid) {
                user.setPassword(new BCryptPasswordEncoder(12).encode(password));
                user.setCode(null);
                userRepository.save(user);
                log.info("Успешно изменен пароль для пользователя с ID: " + user.getId());
                return new ResponseEntity<>(new BooleanResponse(true), HttpStatus.OK);
            } else {
                if (!isCodeValid) {
                    log.warn("Ссылка для восстановления пароля устарела");
                    errors.put("code", "Ссылка для восстановления пароля устарела. <a href=" +
                            "\"" + messageServerLink + serverPort + "/login/restore-password\">Запросить ссылку снова</a>");
                }
                if (!isCaptchaValid) {
                    log.warn("Введенной значение каптчи '" + captcha + "' не совпало с картинкой");
                    errors.put("captcha", "Код с картинки введён неверно");
                }
                if (!isPasswordLengthValid) {
                    log.warn("Пароль '" + password + "' содержит менее 6 символов");
                    errors.put("password", "Пароль короче 6-ти символов");
                }
                return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Параметры запроса заданы не верно: {" +
                    "Code: " + code + ", " +
                    "Password:" + password + ", " +
                    "Captcha:" + captcha + ", " +
                    "Captcha_secret" + captchaSecret + "}");
            throw new Exception("Параметры в запросе заданы не верно");
        }
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

    private boolean isStringParamValid(String text) {
        return !text.isBlank() && !text.equals("") && text != null;
    }
}
