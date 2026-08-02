package de.nihas101.midas.core.userconfig.repository;

import de.nihas101.midas.core.userconfig.entity.UserConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfigEntity, Long> {

    Optional<UserConfigEntity> findByUserIdentifier(final String userIdentifier);
}
