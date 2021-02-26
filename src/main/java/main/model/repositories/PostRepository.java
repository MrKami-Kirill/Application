package main.model.repositories;

import main.model.ModerationStatus;
import main.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus")
    int countAllPostsForModeration(
            @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    int countAllPosts(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT COUNT(t2p.id) FROM TagToPost t2p " +
            "LEFT JOIN Post p ON t2p.idPost.id = p.id " +
            "LEFT JOIN Tag t ON t2p.idTag.id = t.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time " +
            "and t.id = :tagId")
    int countAllPostsByTagId(
            @Param("tagId") int tagId,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT DISTINCT COUNT(p.id) FROM Post p " +
            "WHERE p.text LIKE %:query% OR p.title LIKE %:query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    int countAllPostsByQuery(
            @Param("query") String query,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT COUNT(posts.id) FROM " +
            "(SELECT * FROM posts p " +
            "WHERE DATE(p.time) = ? " +
            "AND p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW()) AS posts", nativeQuery = true)
    int countAllPostsByDate(String date);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "LEFT JOIN TagToPost t2p ON p.id = t2p.idPost.id " +
            "LEFT JOIN Tag t ON t2p.idTag.id = t.id " +
            "WHERE t.name LIKE %:tag% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    int countAllPostsByTag(
            @Param("tag") String tag,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
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
            "AND p.moderationStatus = :moderationStatus")
    int countAllModeratePosts(
            @Param("moderationStatus") ModerationStatus moderationStatus);

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
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.user.id = :userId")
    int countMyPosts(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("userId") Integer userId);

    @Query(value = "SELECT DISTINCT IF((SELECT SUM(view_count) FROM posts " +
            "WHERE is_active = 1 " +
            "AND moderation_status = 'ACCEPTED' " +
            "AND user_id = ?1) IS NOT NULL, (SELECT SUM(view_count) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND user_id = ?1), 0) " +
            "FROM posts", nativeQuery = true)
    int countMyViews(int userId);

    @Query(value = "SELECT DISTINCT IF((SELECT SUM(view_count) FROM posts " +
            "WHERE is_active = 1 " +
            "AND moderation_status = 'ACCEPTED') IS NOT NULL, (SELECT SUM(view_count) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED'), 0) " +
            "FROM posts", nativeQuery = true)
    int countViews();

    @Query(value = "SELECT p.time FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED' " +
            "AND p.user_id = ?1", nativeQuery = true)
    LocalDateTime getMyFirsPublicationTime(int userId, PageRequest pageRequest);

    @Query(value = "SELECT p.time FROM posts p " +
            "WHERE p.is_active = 1 " +
            "AND p.moderation_status = 'ACCEPTED'", nativeQuery = true)
    LocalDateTime getFirsPublicationTime(PageRequest pageRequest);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    Page<Post> getPostsByMode(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN PostComment pc ON p.id = pc.post.id " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    Page<Post> getPopularPosts(
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "WHERE p.text LIKE %:query% OR p.title LIKE %:query% " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    Page<Post> getAllPostsByQuery(
            @Param("query") String query,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time,
            Pageable pageable);

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
            "AND p.time < NOW()", nativeQuery = true)
    List<Post> getAllPostsByDate(String date, Pageable pa);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN TagToPost t2p ON p.id = t2p.idPost.id " +
            "LEFT JOIN Tag t ON t2p.idTag.id = t.id " +
            "WHERE t.name = :tag " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    Page<Post> getAllPostsByTag(
            @Param("tag") String tag,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time,
            Pageable pageable);


    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = true " +
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
            "AND p.moderationStatus = :moderationStatus")
    Page<Post> getAllModeratePosts(
            @Param("moderationStatus") ModerationStatus moderationStatus,
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
            "IF((SELECT COUNT(*) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND user_id = ?) > 0, TRUE, FALSE) " +
            "FROM posts;", nativeQuery = true)
    Integer isPostsExistByUserId(int userId);

}
