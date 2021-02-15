package main.model.repositories;

import main.model.entity.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {
}
