package main.repositories;

import main.model.ModerationStatus;
import main.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'NEW'")
    int countAllPostsForModeration();

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    int countAllPosts();

    @Query(value = "SELECT COUNT(t2p.id) FROM TagToPost t2p " +
            "LEFT JOIN Post p ON t2p.idPost.id = p.id " +
            "LEFT JOIN Tag t ON t2p.idTag.id = t.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP " +
            "AND t.id = :tagId")
    int countAllPostsByTagId(@Param("tagId") int tagId);

    @Query(value = "SELECT DISTINCT COUNT(p.id) FROM Post p " +
            "WHERE p.text LIKE %:query% OR p.title LIKE %:query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    int countAllPostsByQuery(
            @Param("query") String query);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE FUNCTION('DATE_FORMAT', p.time, '%Y-%m-%d') = :date " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    int countAllPostsByDate(
            @Param("date") String date);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "LEFT JOIN TagToPost t2p ON p.id = t2p.idPost.id " +
            "LEFT JOIN Tag t ON t2p.idTag.id = t.id " +
            "WHERE t.name LIKE %:tag% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    int countAllPostsByTag(
            @Param("tag") String tag);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = false " +
            "AND p.user.id = :userId")
    int countMyInActivePosts(
            @Param("userId") Integer userId);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.user.id = :userId")
    int countMyPostsByStatus(
            @Param("userId") Integer userId,
            @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'NEW' ")
    int countAllModeratePosts();

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.moderatorId = :moderatorId " +
            "AND p.moderationStatus IS NOT NULL")
    int countAllModeratePostsByMe(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("moderatorId") Integer moderatorId);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.user.id = :userId")
    int countMyPosts(
            @Param("userId") Integer userId);

    @Query(value = "SELECT CASE WHEN((SELECT SUM(viewCount) FROM Post " +
            "WHERE isActive = true " +
            "AND moderationStatus = 'ACCEPTED' " +
            "AND user.id = :userId) IS NOT NULL) " +
            "THEN (SELECT SUM(p.viewCount) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.user.id = :userId) " +
            "ELSE 0 END " +
            "FROM User")
    int countMyViews(
            @Param("userId") int userId);

    @Query(value = "SELECT CASE WHEN((SELECT SUM(viewCount) FROM Post " +
            "WHERE isActive = true " +
            "AND moderationStatus = 'ACCEPTED') IS NOT NULL) " +
            "THEN (SELECT SUM(p.viewCount) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED') " +
            "ELSE 0 END " +
            "FROM User")
    int countViews();

    @Query(value = "SELECT MIN(p.time) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.user.id = :userId")
    LocalDateTime getMyFirsPublicationTime(
            @Param("userId") int userId);

    @Query(value = "SELECT MIN(p.time) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED'")
    LocalDateTime getFirsPublicationTime();

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    Page<Post> getPostsByMode(
            Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p " +
            "LEFT JOIN (SELECT post_id, SUM(value) AS sum_values " +
            "FROM post_votes GROUP BY post_id) AS sum_votes " +
            "ON p.id = sum_votes.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY sum_values DESC", nativeQuery = true)
    Page<Post> getBestPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p " +
            "LEFT JOIN (SELECT post_id, COUNT(post_id) AS post_count " +
            "FROM post_comments GROUP BY post_id) AS post_comments_count " +
            "ON p.id = post_comments_count.post_id " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() " +
            "ORDER BY post_count DESC", nativeQuery = true)
    Page<Post> getPopularPosts(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "WHERE p.text LIKE %:query% OR p.title LIKE %:query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    Page<Post> getAllPostsByQuery(
            @Param("query") String query,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "WHERE FUNCTION('YEAR', p.time) = :year " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    List<Post> getPostsByYear(
            @Param("year") int year);

    @Query(value = "SELECT DISTINCT FUNCTION('YEAR', p.time) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP " +
            "ORDER BY FUNCTION('YEAR', p.time) DESC")
    List<Integer> getYearsWithAnyPosts();

    @Query(value = "SELECT p FROM Post p " +
            "WHERE FUNCTION('DATE_FORMAT', p.time, '%Y-%m-%d') = :date " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    Page<Post> getAllPostsByDate(
            @Param("date") String date,
            Pageable pa);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN TagToPost t2p ON p.id = t2p.idPost.id " +
            "LEFT JOIN Tag t ON t2p.idTag.id = t.id " +
            "WHERE t.name = :tag " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIMESTAMP")
    Page<Post> getAllPostsByTag(
            @Param("tag") String tag,
            Pageable pageable);


    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = false " +
            "AND p.user.id = :userId")
    Page<Post> getMyInActivePosts(
            @Param("userId") Integer userId,
            Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.user.id = :userId")
    Page<Post> getMyPostByStatus(
            @Param("userId") Integer userId,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'NEW'")
    Page<Post> getAllModeratePosts(
            Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.moderatorId = :moderatorId " +
            "AND p.moderationStatus IS NOT NULL")
    Page<Post> getAllModeratePostsByMe(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("moderatorId") Integer moderatorId,
            Pageable pageable);

    @Query(value = "SELECT " +
            "CASE WHEN ((SELECT COUNT(id) FROM Post " +
            "WHERE isActive = true " +
            "AND moderationStatus = 'ACCEPTED' " +
            "AND user.id = :userId) > 0) THEN TRUE " +
            "ELSE FALSE END " +
            "FROM Post")
    boolean isPostsExistByUserId(@Param("userId") int userId);

}
