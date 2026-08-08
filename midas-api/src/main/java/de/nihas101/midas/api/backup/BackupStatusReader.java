package de.nihas101.midas.api.backup;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BackupStatusReader {
    Optional<LocalDateTime> getLastSuccessAt();
}
