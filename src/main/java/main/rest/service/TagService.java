package main.rest.service;

import lombok.extern.log4j.Log4j2;
import main.rest.api.response.Response;
import main.rest.api.response.GetTagResponse;
import main.rest.model.entity.Tag;
import main.rest.model.repositories.PostRepository;
import main.rest.model.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Log4j2
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<Response> getTags(String query) {
        if (query == null || query.equals("") || query.isBlank()) {
            return getAllTags();
        } else {
            return getAllTagsByQuery(query);
        }
    }

    public ResponseEntity<Response> getAllTags() {
        List<Tag> tags = tagRepository.getAllTags();
        return getResponseEntityByTags(tags);
    }

    private ResponseEntity<Response> getAllTagsByQuery(String query) {
        List<Tag> tags = tagRepository.getAllTagsByQuery(query);
        return getResponseEntityByTags(tags);
    }

    private ResponseEntity<Response> getResponseEntityByTags(List<Tag> tags) {
        HashMap<String, Double> responseMap = new HashMap<>();
        if (!tags.isEmpty()) {
            Integer maxTagCount = tagRepository.getMaxTagCount();
            Integer countAllPosts = postRepository.countAllPosts();
            Double k = 1 / ((double) maxTagCount / (double) countAllPosts);
            for (Tag tag : tags) {
                Double weight = (double) postRepository.countAllPostsByTagId(tag.getId()) / (double) countAllPosts;
                Double normalWeight = Math.round(weight * k * 100.0) / 100.0;
                responseMap.put(tag.getName(), normalWeight);
            }
            ResponseEntity<Response> response = new ResponseEntity<>(new GetTagResponse(responseMap), HttpStatus.OK);
            log.info("Return response " + (response.getBody() == null ? "with empty body"
                    : "with tags: " + response.getBody().toString()));
            return response;

        } else {
            log.info("Tags not found!");
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

}
