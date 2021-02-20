package main.model.repositories;

import main.model.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'NEW'", nativeQuery = true)
    int countAllPostsForModeration();

    @Query(value = "SELECT COUNT(posts.id) " +
            "FROM (SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW()) AS posts", nativeQuery = true)
    int countAllPosts();

    @Query(value = "SELECT COUNT(*) " +
            "FROM tag2post t2p " +
            "LEFT JOIN posts p ON t2p.post_id = p.id " +
            "LEFT JOIN tags t ON t2p.tag_id = t.id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "AND t.id = ?", nativeQuery = true)
    int countAllPostsByTagId(int tagId);

    @Query(value = "SELECT COUNT(posts.id) " +
            "FROM (SELECT * FROM posts p " +
            "WHERE (p.text LIKE %?1% OR p.title LIKE %?1%) " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW()) AS posts", nativeQuery = true)
    int countAllPostsByQuery(String query);

    @Query(value = "SELECT COUNT(posts.id) FROM " +
            "(SELECT * FROM posts p " +
            "WHERE DATE(p.time) = ? " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW()) AS posts", nativeQuery = true)
    int countAllPostsByDate(String date);

    @Query(value = "SELECT COUNT(*) " +
            "FROM tag2post t2p " +
            "LEFT JOIN posts p ON t2p.post_id = p.id " +
            "LEFT JOIN tags t ON t2p.tag_id = t.id " +
            "WHERE (t.name LIKE %?%) " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW()", nativeQuery = true)
    int countAllPostsByTag(String tag);

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 0 " +
            "AND p.user_id = ?1", nativeQuery = true)
    int countMyInActivePosts(Integer userId);

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'NEW' " +
            "AND p.user_id = ?1", nativeQuery = true)
    int countMyPendingPosts(Integer userId);

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'DECLINED' " +
            "AND p.user_id = ?1", nativeQuery = true)
    int countMyDeclinedPosts(Integer userId);

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.user_id = ?1", nativeQuery = true)
    int countMyPublishedPosts(Integer userId);

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'NEW'", nativeQuery = true)
    int countAllModeratePosts();

    @Query(value = "SELECT COUNT(*) FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = ?1 " +
            "AND p.moderator_id = ?2 " +
            "AND p.moderator_id IS NOT NULL", nativeQuery = true)
    int countAllModeratePostsByMe(String status, Integer moderatorId);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getRecentPosts(Integer offset, Integer limit);

    @Query(value = "SELECT p.* FROM posts AS p " +
            "LEFT JOIN (SELECT post_id, COUNT(post_id) AS post_counts FROM post_comments GROUP BY post_id) AS popular_posts ON p.id = popular_posts.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getPopularPosts(Integer offset, Integer limit);

    @Query(value = "SELECT p.* FROM posts AS p " +
            "LEFT JOIN (SELECT post_id, SUM(value) AS sum_values FROM post_votes GROUP BY post_id) AS best_posts ON p.id = best_posts.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getBestPosts(Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getEarlyPosts(Integer offset, Integer limit);

    @Query(value = "SELECT DISTINCT * FROM posts p " +
            "WHERE (p.text LIKE %?1% OR p.title LIKE %?1%) " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getAllPostsByQuery(String query, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p  " +
            "WHERE YEAR(p.time) = ?", nativeQuery = true)
    List<Post> getPostsByYear(int year);

    @Query(value = "SELECT DISTINCT YEAR(p.time) AS post_year " +
            "FROM posts p ORDER BY post_year DESC", nativeQuery = true)
    List<Integer> getYearsWithAnyPosts();


    @Query(value = "SELECT * FROM posts p " +
            "WHERE DATE(p.time) = ?1 " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getAllPostsByDate(String date, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "LEFT JOIN tag2post t2p ON p.id = t2p.post_id " +
            "LEFT JOIN tags t ON t2p.tag_id = t.id " +
            "WHERE t.name LIKE %?1% " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getAllPostsByTag(String tag, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 0 " +
            "AND p.user_id = ?1 " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getMyInActivePosts(Integer userId, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'NEW' " +
            "AND p.user_id = ?1 " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getMyPendingPosts(Integer userId, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'DECLINED' " +
            "AND p.user_id = ?1 " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getMyDeclinedPosts(Integer userId, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.user_id = ?1 " +
            "ORDER BY p.time DESC LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Post> getMyPublishedPosts(Integer userId, Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'NEW' " +
            "ORDER BY p.time DESC LIMIT ?2 OFFSET ?1", nativeQuery = true)
    List<Post> getAllModeratePosts(Integer offset, Integer limit);

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = ?1 " +
            "AND p.moderator_id = ?2 " +
            "AND p.moderator_id IS NOT NULL " +
            "ORDER BY p.time DESC LIMIT ?4 OFFSET ?3", nativeQuery = true)
    List<Post> getAllModeratePostsByMe(String status, Integer moderatorId, Integer offset, Integer limit);

}
