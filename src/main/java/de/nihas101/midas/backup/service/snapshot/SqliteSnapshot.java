package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class SqliteSnapshot implements Snapshot {

    private final JdbcTemplate jdbcTemplate;
    private final ZipOutputStream zos; // TODO: Abstract away
    private final SqliteDatabaseLocation databaseLocation;
    private final String snapshotFilename;

    public SqliteSnapshot(
            final JdbcTemplate jdbcTemplate,
            final ZipOutputStream zos,
            final SqliteDatabaseLocation databaseLocation
    ) {
        this(
                jdbcTemplate,
                zos,
                databaseLocation,
                "backup_snapshot.db"
        );
    }

    @Override
    public void create() throws IOException {
        createDbSnapshot();

        Path snapshotPath = Paths.get(snapshotFilename);
        String dbName = databaseLocation.databaseLocation();
        log.info("Adding database {} to zip...", dbName);
        zos.putNextEntry(new ZipEntry(dbName));
        Files.copy(snapshotPath, zos);
        zos.closeEntry();
    }

    private void createDbSnapshot() {
        cleanupSnapshot();
        log.info("Creating database snapshot via VACUUM INTO...");
        jdbcTemplate.execute("VACUUM INTO '" + snapshotFilename + "'");
    }

    private void cleanupSnapshot() {
        try {
            Files.deleteIfExists(Paths.get(snapshotFilename));
        } catch (IOException e) {
            log.warn("Failed to cleanup snapshot file: {}", e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        cleanupSnapshot();
    }
}
