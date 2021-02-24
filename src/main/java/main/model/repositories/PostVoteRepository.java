package main.model.repositories;

import main.model.ModerationStatus;
import main.model.entity.Post;
import main.model.entity.PostVote;
import main.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    @Query(value = "SELECT COUNT(pv.id) FROM PostVote pv " +
            "LEFT JOIN Post p ON pv.post.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND pv.user.id = :userId " +
            "AND pv.value = 1")
    int countMyLikes(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("userId") Integer userId);

    @Query(value = "SELECT COUNT(pv.id) FROM PostVote pv " +
            "LEFT JOIN Post p ON pv.post.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND pv.user.id = :userId " +
            "AND pv.value = -1")
    int countMyDislikes(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("userId") Integer userId);

    @Query(value = "SELECT COUNT(pv.id) FROM PostVote pv " +
            "LEFT JOIN Post p ON pv.post.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND pv.value = 1")
    int countLikes(@Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT COUNT(pv.id) FROM PostVote pv " +
            "LEFT JOIN Post p ON pv.post.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND pv.value = -1")
    int countDislikes(@Param("moderationStatus") ModerationStatus moderationStatus);

    Optional<PostVote> findByUserAndPostAndValue(User user, Post post, byte value);

    Optional<PostVote> findByUserAndPost(User user, Post post);
}
