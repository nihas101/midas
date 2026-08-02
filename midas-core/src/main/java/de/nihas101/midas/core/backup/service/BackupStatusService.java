package de.nihas101.midas.core.backup.service;

import de.nihas101.midas.api.backup.BackupStatusReader;
import de.nihas101.midas.api.backup.BackupStatusWriter;
import de.nihas101.midas.persistance.backup.BackupStatusEntity;
import de.nihas101.midas.persistance.backup.BackupStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BackupStatusService implements BackupStatusReader, BackupStatusWriter {

    private static final Integer DEFAULT_ID = 1;
    private final BackupStatusRepository repository;

    @Override
    public Optional<LocalDateTime> getLastSuccessAt() {
        return repository.findById(DEFAULT_ID)
                .map(BackupStatusEntity::getLastSuccessAt);
    }

    @Override
    public void updateLastSuccessAt(final LocalDateTime timestamp) {
        final BackupStatusEntity entity = repository.findById(DEFAULT_ID)
                .orElse(new BackupStatusEntity(DEFAULT_ID, null));
        entity.setLastSuccessAt(timestamp);
        repository.save(entity);
    }
}
