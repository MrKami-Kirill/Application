package main.rest.model.repositories;

import main.rest.model.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM captcha_codes " +
            "WHERE time < ?", nativeQuery = true)
    void deleteExpiredCaptchaCodes(LocalDateTime time);

    @Query(value = "SELECT * FROM captcha_codes " +
            "WHERE time < ?", nativeQuery = true)
    List<CaptchaCode> getExpiredCaptchaCodes(LocalDateTime time);

    @Query(value = "SELECT * FROM captcha_codes c" +
            " WHERE c.secret_code = ?", nativeQuery = true)
    CaptchaCode getCaptchaBySecretCode(String secretCode);
}
