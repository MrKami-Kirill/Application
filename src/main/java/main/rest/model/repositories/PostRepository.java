package main.rest.model.repositories;

import main.rest.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(p.id) " +
            "FROM posts p " +
            "WHERE p.isActive = 1 AND " +
            "p.moderation_status = 'NEW'", nativeQuery = true)
    int countPostsForModeration();

    @Query(value = "SELECT COUNT(actual_posts.id) " +
            "FROM (SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW()) AS actual_posts", nativeQuery = true)
    int countAllPostsAtSite();

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getRecentPosts(int offset, int limit);

    @Query(value = "SELECT p.* FROM posts AS p " +
            "LEFT JOIN (SELECT post_id, COUNT(post_id) AS post_counts FROM post_comments GROUP BY post_id) AS popular_posts ON p.id = popular_posts.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY post_counts DESC " +
            "LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getPopularPosts(int offset, int limit);

    @Query(value = "SELECT p.* FROM posts AS p " +
            "LEFT JOIN (SELECT post_id, SUM(value) AS sum_values FROM post_votes GROUP BY post_id) AS best_posts ON p.id = best_posts.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY sum_values DESC " +
            "LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getBestPosts(int offset, int limit);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time ASC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getEarlyPosts(int offset, int limit);

}
