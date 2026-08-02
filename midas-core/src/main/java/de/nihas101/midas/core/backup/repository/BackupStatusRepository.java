package de.nihas101.midas.core.backup.repository;

import de.nihas101.midas.core.backup.entity.BackupStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupStatusRepository extends JpaRepository<BackupStatusEntity, Integer> {
}
