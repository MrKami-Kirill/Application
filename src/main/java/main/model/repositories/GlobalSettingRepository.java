package main.model.repositories;

import main.model.entity.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {

    @Query(value = "SELECT gs.value FROM global_settings gs " +
            "WHERE gs.code = ?", nativeQuery = true)
    String getGlobalSettingValue(String code);
}
