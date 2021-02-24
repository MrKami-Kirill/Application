package main.model.repositories;

import main.model.ModerationStatus;
import main.model.entity.Tag;
import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {


    @Query(value = "SELECT DISTINCT t FROM Tag t " +
            "JOIN TagToPost t2p ON t.id = t2p.idTag.id " +
            "JOIN Post p ON t2p.idPost.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time " +
            "ORDER BY t.id DESC")
    List<Tag> getAllTags(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT DISTINCT t FROM Tag t " +
            "JOIN TagToPost t2p ON t.id = t2p.idTag.id " +
            "JOIN Post p ON t2p.idPost.id = p.id " +
            "WHERE t.name LIKE %:query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time " +
            "ORDER BY t.id DESC")
    List<Tag> getAllTagsByQuery(
            @Param("query") String query,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

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
