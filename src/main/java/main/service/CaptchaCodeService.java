package main.service;

import com.github.cage.Cage;
import com.github.cage.ObjectRoulette;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import com.github.cage.image.RgbColorGenerator;
import com.github.cage.token.RandomTokenGenerator;
import lombok.extern.log4j.Log4j2;
import main.api.response.GetCaptchaCodeResponse;
import main.api.response.Response;
import main.model.entity.CaptchaCode;
import main.model.repositories.CaptchaCodeRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
@Log4j2
public class CaptchaCodeService {

    @Value("${captcha.secret_code_length}")
    private int secretCodeLength;
    @Value("${captcha.timeout}")
    private int timeout;
    @Value("${captcha.image.title}")
    private String captchaImageTitle;
    @Value("${captcha.image.format}")
    private String captchaFormat;
    @Value("${captcha.image.width}")
    private int captchaImageWidth;
    @Value("${captcha.image.height}")
    private int captchaImageHeight;
    @Value("${captcha.image.text.length}")
    private int captchaImageTextLength;



    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;

    public ResponseEntity<Response> getCaptcha() {

        LocalDateTime captchaExpiredTime = LocalDateTime.now().minusHours(timeout);
        captchaCodeRepository.deleteExpiredCaptchaCodes(captchaExpiredTime);
        log.info("Удалены устаревшие каптчи из БД {" + captchaCodeRepository.getExpiredCaptchaCodes(captchaExpiredTime).toString() + "}");

        String secretCode = generateSecretCode();
        log.info("Сгенерирован новый сегретный код для каптчи: " + secretCode);
        Cage cage = createCage();
        String token = cage.getTokenGenerator().next();
        token = token.substring(0, captchaImageTextLength);
        log.info("Сгенерирован код каптчи: " + token);

        byte[] encodedBytes = Base64.getEncoder().encode(cage.draw(token));
        String captchaImageBase64 = captchaImageTitle + ", " + new String(encodedBytes, StandardCharsets.UTF_8);
        log.info("Код каптчи сконвертирован в base64 {" + captchaImageBase64 + "}");
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), token, secretCode);
        captchaCodeRepository.save(captchaCode);
        log.info("Создана новая запись в captcha_code с id=" + captchaCode.getId());
        ResponseEntity<Response> response = new ResponseEntity<>(new GetCaptchaCodeResponse(secretCode, captchaImageBase64), HttpStatus.OK);
        log.info("Направляем ответ на запрос /api/auth/captcha cо следующими параметрами: {" +
                "HttpStatus: " + response.getStatusCode() + ", " +
                response.getBody());
        return response;

    }

    private Cage createCage() {
        Random random = new Random();
        Painter painter = new Painter(
                captchaImageWidth,
                captchaImageHeight,
                Color.WHITE, //bgColor
                Painter.Quality.MAX, //quality
                new EffectConfig(),
                random);
        int captchaFontHeight = painter.getHeight();
        Font[] captchaRandomFont = new Font[] {
                new Font(Font.SERIF, Font.PLAIN, captchaFontHeight),
                new Font(Font.SANS_SERIF, Font.BOLD, captchaFontHeight),
                new Font(Font.DIALOG, Font.PLAIN, captchaFontHeight),
                new Font(Font.MONOSPACED, Font.BOLD, captchaFontHeight)};
        Cage cage = new Cage(
                painter,
                new ObjectRoulette<>(random, captchaRandomFont),
                new RgbColorGenerator(random),
                captchaFormat,
                Cage.DEFAULT_COMPRESS_RATIO,
                new RandomTokenGenerator(random),
                random);
        log.info("Сгенерирована новая каптча {" +
                "width: " + captchaImageWidth + ", " +
                "height: " + captchaImageHeight + ", " +
                "format: " + captchaFormat + "}");
        return cage;
    }

    private String generateSecretCode() {
        return RandomStringUtils.random(secretCodeLength, true, true);
    }
}
