package main.service;

import lombok.extern.slf4j.Slf4j;
import main.api.response.Response;
import main.api.response.TagResponse;
import main.model.entity.Post;
import main.model.entity.Tag;
import main.model.entity.TagToPost;
import main.model.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private TagToPostService tagToPostService;

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
            Integer maxTagCount = Collections.max(tagRepository.getMaxTagCount());
            log.info("Получен кол-во публикация (" + maxTagCount + ") для самого популярного тега на сайте");
            Integer countAllPosts = postService.countAllPosts();
            log.info("Получено общее кол-во публикаций на сайте (" + countAllPosts + ")");
            Double k = 1 / ((double) maxTagCount / (double) countAllPosts);
            log.info("Получен коэффициент нормализации k=" + k);
            for (Tag tag : tags) {
                Double weight = (double) postService.countAllPostsByTagId(tag.getId()) / (double) countAllPosts;
                log.info("Получен вес (" + weight + ") для тега '" + tag.getName() + "'");
                Double normalWeight = Math.round(weight * k * 100.0) / 100.0;
                log.info("Получен нормированный вес (" + normalWeight + ") для тега '" + tag.getName() + "'");
                responseMap.put(tag.getName(), normalWeight);
            }
            ResponseEntity<Response> response = new ResponseEntity<>(new TagResponse(responseMap), HttpStatus.OK);
            log.info("Направляем ответ " + (response.getBody() == null ? "с пустым телом"
                    : "с тегами: " + response.getBody().toString()));
            return response;

        } else {
            log.info("Теги не найдены");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public List<String> getAllTagsForStringArray() {
        List<String> tags = new ArrayList<>();
        for (Tag tag : tagRepository.findAll()) {
            tags.add(tag.getName().toUpperCase(Locale.ROOT));
        }
        return tags;
    }

    public List<Tag> getNewTags(List<String> tags, List<String> oldTagNameList) {
        List<Tag> newTags = new ArrayList<>();
        for (String tag : tags) {
            if (!oldTagNameList.contains(tag.toUpperCase(Locale.ROOT))) {
                log.info("При создании поста добавлен новый тэг '" + tag + "'");
                Tag newTag = new Tag(tag);
                tagRepository.save(newTag);
                newTags.add(newTag);
            }
        }

        return newTags;
    }

    public void addNewTagsByPost(List<String> newTagNameList, Post newPost) {

        List<String> newTagNameUpperList = new ArrayList<>();
        List<String> oldTagNameUpperList = new ArrayList<>();
        List<Tag> oldTagList = tagRepository.findAll();

        for (String newTagNameUpper : newTagNameList) {
            newTagNameUpperList.add(newTagNameUpper.toUpperCase().trim());
        }

        for (Tag oldTag : oldTagList) {
            oldTagNameUpperList.add(oldTag.getName().toUpperCase().trim());
        }

        for (String newTagName : newTagNameList) {
            if (!oldTagNameUpperList.contains(newTagName.toUpperCase().trim())) {
                Tag newTag = new Tag(newTagName.trim());
                tagRepository.save(newTag);
                log.info("При создании поста добавлен новый тэг '" + newTagName + "'");
            }
        }

        for (Tag tag : tagRepository.findAll()) {
            if (newTagNameUpperList.contains(tag.getName().toUpperCase(Locale.ROOT).trim())) {
                tagToPostService.getTagToPostRepository().save(new TagToPost(tag, newPost));
                log.info("Пост с ID=" + newPost.getId() + " связан с тегом '" + tag.getName() + "'");
            }
        }
    }

}
