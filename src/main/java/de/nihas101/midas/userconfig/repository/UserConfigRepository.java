package de.nihas101.midas.userconfig.repository;

import de.nihas101.midas.userconfig.entity.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {

    Optional<UserConfig> findByUserIdentifier(String userIdentifier);
}
