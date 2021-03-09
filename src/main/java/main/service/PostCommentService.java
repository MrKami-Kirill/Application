package main.service;

import lombok.extern.slf4j.Slf4j;
import main.model.dto.request.PostCommentRequest;
import main.model.dto.response.BadRequestMessageResponse;
import main.model.dto.response.PostCommentResponse;
import main.model.dto.response.Response;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.User;
import main.repositories.PostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class PostCommentService {

    @Value("${post_comment.min_length}")
    private int minCommentLength;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    public ResponseEntity<Response> addComment(PostCommentRequest commentRequest, HttpSession session) throws Exception {

        HashMap<String, String> errors = new HashMap<>();

        User user = userService.getUserBySession(session);
        if (user == null) {
            log.warn("Не найден пользователь для сессии с ID=" + session.getId());
            throw new Exception("Пользователь не найден");
        }

        Integer postId = commentRequest.getPostId();
        Integer parentId = commentRequest.getParentId();
        String text = commentRequest.getText();

        boolean isTextCommentValid = isTextValid(text);

        Post post = postService.getPostRepository().findById(postId).orElse(null);
        if (post == null) {
            log.warn("Ошибка! Пост с ID=" + postId + " не найден");
            throw new Exception("Пост не найден");
        }

        PostComment newPostComment;
        ResponseEntity<Response> response;

        if (isTextCommentValid) {
            if (parentId != null) {
                PostComment parentPostComment = postCommentRepository.findById(parentId).orElse(null);
                if (parentPostComment != null) {
                    newPostComment = new PostComment(LocalDateTime.now(), text, parentPostComment, user, post);
                    postCommentRepository.save(newPostComment);
                    log.info("Комментарий c ID=" + newPostComment.getId() + " успешно добавлен к посту");
                    response = new ResponseEntity<>(new PostCommentResponse(newPostComment), HttpStatus.OK);
                } else {
                    log.warn("Ошибка! Родительский комментарий с ID=" + parentId + " не найден");
                    throw new Exception("Невозможно оставить комментарий к несуществующему комментарию");
                }
            } else {
                newPostComment = new PostComment(LocalDateTime.now(), text, user, post);
                postCommentRepository.save(newPostComment);
                log.info("Комментарий c ID=" + newPostComment.getId() + " успешно добавлен к посту");
                response = new ResponseEntity<>(new PostCommentResponse(newPostComment), HttpStatus.OK);

            }
        } else {
            log.warn("Ошибка! Текст комментария не задан или слишком короткий. Минимальная длина комментария - " + minCommentLength + " символа");
            errors.put("text", "Текст комментария не задан или слишком короткий");
            return new ResponseEntity<>(new BadRequestMessageResponse(errors), HttpStatus.BAD_REQUEST);
        }
        log.info("Направляется ответ на запрос /api/post/comment cо следующими параметрами: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }


    public boolean isTextValid(String text) {
        if (text.length() < minCommentLength || text.isBlank() || text.equals("")) {
            return false;
        }

        return true;
    }
}
