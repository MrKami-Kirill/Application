package main.repositories;

import main.model.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CaptchaCode cc " +
            "WHERE cc.time < :time")
    void deleteExpiredCaptchaCodes(
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT cc FROM CaptchaCode cc " +
            "WHERE cc.time < :time")
    List<CaptchaCode> getExpiredCaptchaCodes(
            @Param("time") LocalDateTime time);

    @Query(value = "SELECT cc FROM CaptchaCode cc " +
            "WHERE cc.secretCode = :secretCode")
    CaptchaCode getCaptchaBySecretCode(
            @Param("secretCode") String secretCode);
}
