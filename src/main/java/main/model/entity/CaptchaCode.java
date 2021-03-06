package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@NoArgsConstructor
@AllArgsConstructor
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
