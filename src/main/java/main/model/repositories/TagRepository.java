package main.model.repositories;

import main.model.ModerationStatus;
import main.model.entity.Tag;
import main.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "WHERE t.name LIKE :query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time " +
            "ORDER BY t.id DESC")
    List<Tag> getAllTagsByQuery(
            @Param("query") String query,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT COUNT(t2p.idTag.id) FROM TagToPost t2p " +
            "JOIN Post p ON t2p.idPost.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time " +
            "GROUP BY t2p.idTag.id")
    List<Integer> getMaxTagCount(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);
}
