package de.nihas101.midas.persistance.backup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupStatusRepository extends JpaRepository<BackupStatusEntity, Integer> {
}
