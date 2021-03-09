package main.repositories;

import main.model.entity.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {

    @Modifying
    @Query(value = "DELETE FROM TagToPost t2p where t2p.id = :id")
    void deleteById(@Param("id") Integer id);
}
