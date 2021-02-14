package main.rest.model.repositories;

import main.rest.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {


    @Query(value = "SELECT " +
            "IF((SELECT COUNT(*) FROM users WHERE email = ?) > 0, TRUE, FALSE) " +
            "FROM users;", nativeQuery = true)
    Integer isUserExistByEmail(String email);
}
