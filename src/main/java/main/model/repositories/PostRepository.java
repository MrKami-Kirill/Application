package main.model.repositories;

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
            "AND p.moderationStatus = :moderationStatus")
    int countAllPostsForModeration(
            @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
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

    @Query(value = "SELECT COUNT(p.id) FROM Post p " +
            "WHERE FUNCTION('DATE_FORMAT', p.time, '%Y-%m-%d') = :date " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    int countAllPostsByDate(
            @Param("date") String date,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time);

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

    @Query(value = "SELECT CASE WHEN((SELECT SUM(viewCount) FROM Post " +
            "WHERE isActive = true " +
            "AND moderationStatus = :moderationStatus " +
            "AND user.id = :userId) IS NOT NULL) " +
            "THEN (SELECT SUM(p.viewCount) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.user.id = :userId) " +
            "ELSE 0 END " +
            "FROM User")
    int countMyViews(
            @Param("userId") int userId,
            @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT CASE WHEN((SELECT SUM(viewCount) FROM Post " +
            "WHERE isActive = true " +
            "AND moderationStatus = :moderationStatus) IS NOT NULL) " +
            "THEN (SELECT SUM(p.viewCount) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus) " +
            "ELSE 0 END " +
            "FROM User")
    int countViews(
            @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT MIN(p.time) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.user.id = :userId")
    LocalDateTime getMyFirsPublicationTime(
            @Param("userId") int userId,
            @Param("moderationStatus") ModerationStatus moderationStatus);

    @Query(value = "SELECT MIN(p.time) FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus")
    LocalDateTime getFirsPublicationTime(
            @Param("moderationStatus") ModerationStatus moderationStatus);

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

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "WHERE FUNCTION('YEAR', p.time) = :year")
    List<Post> getPostsByYear(
            @Param("year") int year);

    @Query(value = "SELECT DISTINCT FUNCTION('YEAR', p.time) FROM Post p " +
            "ORDER BY FUNCTION('YEAR', p.time) DESC")
    List<Integer> getYearsWithAnyPosts();

    @Query(value = "SELECT p FROM Post p " +
            "WHERE FUNCTION('DATE_FORMAT', p.time, '%Y-%m-%d') = :date " +
            "AND p.isActive = true " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND p.time < :time")
    Page<Post> getAllPostsByDate(
            @Param("date") String date,
            @Param("moderationStatus") ModerationStatus moderationStatus,
            @Param("time") LocalDateTime time,
            Pageable pa);

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
            "CASE WHEN ((SELECT COUNT(id) FROM Post " +
            "WHERE isActive = true " +
            "AND moderationStatus = :moderationStatus " +
            "AND user.id = :userId) > 0) THEN TRUE " +
            "ELSE FALSE END " +
            "FROM Post")
    boolean isPostsExistByUserId(
            @Param("userId") int userId,
            @Param("moderationStatus") ModerationStatus moderationStatus);

}
