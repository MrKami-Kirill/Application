package main.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import main.model.entity.Post;
import main.model.entity.TagToPost;
import main.model.repositories.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Data
public class TagToPostService {

    @Autowired
    private TagToPostRepository tagToPostRepository;

    public void deleteTagToPostByPost(Post post) {
        for (TagToPost t2p : post.getTagToPosts()) {
            tagToPostRepository.deleteById(t2p.getId());
            log.info("Связка поста с тэгом (ID=" + t2p.getId() + ") успешно удалена");
        }
    }
}
