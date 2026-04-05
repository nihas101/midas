package de.nihas101.midas.backup.service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BackupStatusReader {
    Optional<LocalDateTime> getLastSuccessAt();
}
