package main.model.repositories;

import main.model.entity.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {

    @Modifying
    @Query(value = "delete from tag2post t2p where t2p.id = ?", nativeQuery = true)
    void deleteById(Integer id);
}
