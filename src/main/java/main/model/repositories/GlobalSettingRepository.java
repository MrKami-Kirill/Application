package main.model.repositories;

import main.model.entity.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {

    @Query(value = "SELECT gs.value FROM GlobalSetting gs " +
            "WHERE gs.code = :code")
    String getGlobalSettingValue(
            @Param("code") String code);
}
