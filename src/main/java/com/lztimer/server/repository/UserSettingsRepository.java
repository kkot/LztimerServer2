package com.lztimer.server.repository;

import com.lztimer.server.entity.User;
import com.lztimer.server.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the UserSettings entity.
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings,Long> {
    UserSettings findUserSettingsByUser(User user);
}
