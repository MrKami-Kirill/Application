package main.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@NamedQueries({
        @NamedQuery(name = "CaptchaCode.findAll", query = "SELECT b FROM CaptchaCode b")
        , @NamedQuery(name = "CaptchaCode.findById", query = "SELECT b FROM CaptchaCode b WHERE b.id = :id")
})
public class CaptchaCode implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "time", columnDefinition = "DATETIME")
    @NotNull
    private LocalDateTime time;

    @Column(name = "code", columnDefinition = "TINYTEXT")
    @NotNull
    private String code;

    @Column(name = "secret_code", columnDefinition = "TINYTEXT")
    @NotNull
    private String secretCode;

    public CaptchaCode() {
    }

    public CaptchaCode(@NotNull LocalDateTime time, @NotNull String code, @NotNull String secretCode) {
        this.time = time;
        this.code = code;
        this.secretCode = secretCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }
}
