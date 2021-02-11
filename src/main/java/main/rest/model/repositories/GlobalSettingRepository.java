package main.rest.model.repositories;

import main.rest.model.entity.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {
}
