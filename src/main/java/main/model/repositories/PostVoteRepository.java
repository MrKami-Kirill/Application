package main.model.repositories;

import main.model.entity.Post;
import main.model.entity.PostVote;
import main.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    @Query(value = "SELECT COUNT(*) FROM post_votes pv " +
            "LEFT JOIN posts p ON pv.post_id = p.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND pv.user_id = ?1 " +
            "AND value = 1", nativeQuery = true)
    int countMyLikes(int userId);

    @Query(value = "SELECT COUNT(*) FROM post_votes pv " +
            "LEFT JOIN posts p ON pv.post_id = p.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND pv.user_id = ?1 " +
            "AND value = -1", nativeQuery = true)
    int countMyDislikes(int userId);

    @Query(value = "SELECT COUNT(*) FROM post_votes pv " +
            "LEFT JOIN posts p ON pv.post_id = p.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND value = 1", nativeQuery = true)
    int countLikes();

    @Query(value = "SELECT COUNT(*) FROM post_votes pv " +
            "LEFT JOIN posts p ON pv.post_id = p.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND value = -1", nativeQuery = true)
    int countDislikes();

    Optional<PostVote> findByUserAndPostAndValue(User user, Post post, byte value);

    Optional<PostVote> findByUserAndPost(User user, Post post);
}
