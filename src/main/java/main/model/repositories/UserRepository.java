package main.model.repositories;

import main.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


    @Query(value = "SELECT " +
            "IF((SELECT COUNT(*) FROM users WHERE LOWER(email) = ?) > 0, TRUE, FALSE) " +
            "FROM users;", nativeQuery = true)
    Integer isUserExistByEmail(String email);

    Optional<User> findUserByCode(String code);

    Optional<User> findByEmail(String email);
}
