package main.rest.model.repositories;

import main.rest.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {


    @Query(value = "SELECT DISTINCT t.*" +
            "FROM tags t " +
            "JOIN tag2post t2p ON t.id = t2p.tag_id " +
            "JOIN posts p ON t2p.post_id = p.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY t.id DESC", nativeQuery = true)
    List<Tag> getAllTags();

    @Query(value = "SELECT DISTINCT t.*" +
            "FROM tags t " +
            "JOIN tag2post t2p ON t.id = t2p.tag_id " +
            "JOIN posts p ON t2p.post_id = p.id " +
            "WHERE (t.name LIKE %?%) " +
            "p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY t.id DESC", nativeQuery = true)
    List<Tag> getAllTagsByQuery(String query);

    @Query(value = "SELECT MAX(tag_count) " +
            "FROM (" +
            "SELECT COUNT(post_id) AS tag_count " +
            "FROM tag2post t2p " +
            "INNER JOIN posts p ON t2p.post_id = p.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "GROUP BY t2p.tag_id) " +
            "AS max_tag_count", nativeQuery = true)
    int getMaxTagCount();
}
