package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
public class SqliteSnapshot implements Snapshot {

    private final ArchiveWriter archiveWriter;
    private final SqliteDatabaseLocation databaseLocation;
    private final String snapshotFilename;
    private final CleanupSnapshot cleanupSnapshot;
    private final CreateSnapshot createSnapshot;

    public SqliteSnapshot(
            final JdbcTemplate jdbcTemplate,
            final ArchiveWriter archiveWriter,
            final SqliteDatabaseLocation databaseLocation
    ) {
        this(
                jdbcTemplate,
                archiveWriter,
                databaseLocation,
                "midas_backup.db"
        );
    }

    public SqliteSnapshot(
            final JdbcTemplate jdbcTemplate,
            final ArchiveWriter archiveWriter,
            final SqliteDatabaseLocation databaseLocation,
            final String snapshotFilename
    ) {
        this(
                jdbcTemplate,
                archiveWriter,
                databaseLocation,
                snapshotFilename,
                new CleanupSnapshotFile(
                        snapshotFilename
                )
        );
    }

    public SqliteSnapshot(
            final JdbcTemplate jdbcTemplate,
            final ArchiveWriter archiveWriter,
            final SqliteDatabaseLocation databaseLocation,
            final String snapshotFilename,
            final CleanupSnapshotFile cleanupSnapshot
    ) {
        this(
                archiveWriter,
                databaseLocation,
                snapshotFilename,
                cleanupSnapshot,
                new CreateSnapshotFile(
                        cleanupSnapshot,
                        jdbcTemplate,
                        snapshotFilename
                )
        );
    }

    @Override
    public void create() throws IOException {
        createSnapshot.create();

        Path snapshotPath = Paths.get(snapshotFilename);
        String dbName = databaseLocation.databaseLocation();
        log.info("Adding database {} to zip...", dbName);
        archiveWriter.add(snapshotPath.toFile(), dbName);
    }

    @Override
    public void close() throws Exception {
        cleanupSnapshot.cleanup();
    }

}
