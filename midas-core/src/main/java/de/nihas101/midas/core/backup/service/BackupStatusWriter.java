package de.nihas101.midas.core.backup.service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface BackupStatusWriter {
    @Transactional
    void updateLastSuccessAt(final LocalDateTime timestamp);
}
