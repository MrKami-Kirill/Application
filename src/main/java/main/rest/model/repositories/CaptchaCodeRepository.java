package main.rest.model.repositories;

import main.rest.model.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
}
