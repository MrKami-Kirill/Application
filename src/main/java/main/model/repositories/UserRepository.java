package main.model.repositories;

import main.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


    @Query(value = "SELECT " +
            "CASE WHEN ((SELECT COUNT(id) FROM User WHERE LOWER(email) = LOWER(:email)) > 0) THEN TRUE " +
            "ELSE FALSE END " +
            "FROM User")
    boolean isUserExistByEmail(
            @Param("email") String email);

    Optional<User> findUserByCode(String code);

    Optional<User> findByEmail(String email);
}
