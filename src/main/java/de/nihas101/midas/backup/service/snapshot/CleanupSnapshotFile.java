package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
public class CleanupSnapshotFile implements CleanupSnapshot {

    private final String snapshotFilename;

    @Override
    public void cleanup() {
        try {
            Files.deleteIfExists(Paths.get(snapshotFilename));
        } catch (IOException e) {
            log.warn("Failed to cleanup snapshot file: {}", e.getMessage());
        }
    }
}
