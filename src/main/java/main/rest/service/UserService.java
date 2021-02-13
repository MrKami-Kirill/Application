package main.rest.service;

import lombok.extern.log4j.Log4j2;
import main.rest.api.response.AuthUserResponse;
import main.rest.api.response.BadRequestMessageResponse;
import main.rest.api.response.BooleanResponse;
import main.rest.api.response.Response;
import main.rest.model.entity.User;
import main.rest.model.repositories.PostRepository;
import main.rest.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
public class UserService {

    //Пока как заглушка. При реализации авторизации (api/auth/login) будем заполнять (put)
    private Map<String, Integer> sessionId = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<Response> checkAuth(HttpSession session) {
        if (!sessionId.containsKey(session.getId().toString())) {
            log.warn("Error! Session not found!");
            return new ResponseEntity<>(new BooleanResponse(false), HttpStatus.OK);
        } else {
            User user = getUserBySession(session);
            return getResponseEntityByUserExist(user);
        }
    }

    private ResponseEntity<Response> getResponseEntityByUserExist(User user) {
        if (user != null) {
            log.info("User" + user.getName() +" found by session");
            return new ResponseEntity<>(new AuthUserResponse(user, postRepository.countAllPostsForModeration()),
                    HttpStatus.OK);
        } else {
            log.warn("Error! Session not found, user is not authorized");
            return new ResponseEntity<>(new BadRequestMessageResponse("User is not authorized"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public Integer getUserIdBySession(HttpSession session) {
        return sessionId.get(session.getId().toString());
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


}
