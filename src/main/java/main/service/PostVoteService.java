package main.service;

import lombok.extern.slf4j.Slf4j;
import main.model.entity.Post;
import main.model.entity.PostVote;
import main.model.entity.User;
import main.model.repositories.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PostVoteService {

    @Autowired
    private PostVoteRepository postVoteRepository;

    public int countMyLikes(int userId) {
        int likesCount = postVoteRepository.countMyLikes(userId);
        log.info("Получено общее кол-во лайков (" + likesCount + ") для пользователя с ID: " + userId);
        return likesCount;
    }

    public int countMyDislikes(int userId) {
        int dislikesCount = postVoteRepository.countMyDislikes(userId);
        log.info("Получено общее кол-во дислайков (" + dislikesCount + ") для пользователя с ID: " + userId);
        return dislikesCount;
    }

    public int countLikes() {
        int likesCount = postVoteRepository.countLikes();
        log.info("Получено общее кол-во лайков (" + likesCount + ") на сайте");
        return likesCount;
    }

    public int countDislikes() {
        int dislikesCount = postVoteRepository.countDislikes();
        log.info("Получено общее кол-во дислайков (" + dislikesCount + ") на сайте");
        return dislikesCount;
    }

    public boolean vote(User user, Post post, byte value) {
        String strValue = value == 1 ? "Лайк" : "Дислайк";
        PostVote vote = new PostVote(LocalDateTime.now(), value, user, post);
        PostVote currentVote = postVoteRepository.findByUserAndPost(user, post).orElse(null);
        if (currentVote == null) {
            postVoteRepository.save(vote);
            log.info(strValue + " успешно проставлен пользователем с ID: " + user.getId() + " для поста с ID: " + post.getId());
            return true;
        } else {
            byte oppositeValue = (byte) (value == 1 ? -1 : 1);
            String oppositeStrValue = value == 1 ? "Дислайк" : "Лайк";
            PostVote oppositePostVote = postVoteRepository.findByUserAndPostAndValue(user, post, oppositeValue).orElse(null);
            if (oppositePostVote != null) {
                postVoteRepository.delete(oppositePostVote);
                log.info(oppositeStrValue + " успешно удален для поста с ID: " + post.getId());
                postVoteRepository.save(vote);
                log.info(strValue + " успешно проставлен пользователем с ID: " + user.getId() + " для поста с ID: " + post.getId());
                return true;
            } else {
                return false;
            }
        }
    }
}
