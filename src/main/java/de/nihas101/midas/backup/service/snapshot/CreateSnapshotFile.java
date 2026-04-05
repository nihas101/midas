package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@RequiredArgsConstructor
public class CreateSnapshotFile implements CreateSnapshot {

    private final CleanupSnapshotFile cleanupSnapshotFile;
    private final JdbcTemplate jdbcTemplate;
    private final String snapshotFilename;

    @Override
    public void create() {
        if (cleanupSnapshotFile != null) {
            cleanupSnapshotFile.cleanup();
        } else {
            log.warn("No cleanup occurred, because cleanupSnapshotFile is null");
        }
        if (jdbcTemplate == null) {
            throw new RuntimeException("jdbcTemplate is null");
        }
        if (snapshotFilename == null) {
            throw new RuntimeException("snapshotFilename is null");
        }
        log.info("Creating database snapshot via VACUUM INTO...");
        jdbcTemplate.execute("VACUUM INTO '" + snapshotFilename + "'");
    }
}
