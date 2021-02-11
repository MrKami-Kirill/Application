package main.rest.model.repositories;

import main.rest.model.entity.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {
}
