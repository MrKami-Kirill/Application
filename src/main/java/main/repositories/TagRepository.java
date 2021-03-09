package main.repositories;

import main.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {


    @Query(value = "SELECT DISTINCT t FROM Tag t " +
            "JOIN TagToPost t2p ON t.id = t2p.idTag.id " +
            "JOIN Post p ON t2p.idPost.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP " +
            "ORDER BY t.id DESC")
    List<Tag> getAllTags();

    @Query(value = "SELECT DISTINCT t FROM Tag t " +
            "JOIN TagToPost t2p ON t.id = t2p.idTag.id " +
            "JOIN Post p ON t2p.idPost.id = p.id " +
            "WHERE t.name LIKE :query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP " +
            "ORDER BY t.id DESC")
    List<Tag> getAllTagsByQuery(@Param("query") String query);

    @Query(value = "SELECT COUNT(t2p.idTag.id) FROM TagToPost t2p " +
            "JOIN Post p ON t2p.idPost.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP " +
            "GROUP BY t2p.idTag.id")
    List<Integer> getMaxTagCount();
}
