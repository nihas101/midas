package de.nihas101.midas.core.backup.service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BackupStatusReader {
    Optional<LocalDateTime> getLastSuccessAt();
}
