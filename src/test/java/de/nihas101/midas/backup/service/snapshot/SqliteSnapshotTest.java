package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SqliteSnapshotTest {

    private ArchiveWriter archiveWriter;
    private SqliteDatabaseLocation databaseLocation;
    private SqliteSnapshot sqliteSnapshot;
    private CleanupSnapshot cleanupSnapshot;
    private CreateSnapshot createSnapshot;
    private static final String TEST_SNAPSHOT_FILE = "test_snapshot.db";

    @BeforeEach
    void setUp() {
        archiveWriter = mock(ArchiveWriter.class);
        databaseLocation = mock(SqliteDatabaseLocation.class);
        cleanupSnapshot = mock(CleanupSnapshot.class);
        createSnapshot = mock(CreateSnapshot.class);
        sqliteSnapshot = new SqliteSnapshot(
                archiveWriter,
                databaseLocation,
                TEST_SNAPSHOT_FILE,
                cleanupSnapshot,
                createSnapshot
        );
    }

    @Test
    void createExecutesVacuumAndAddsToArchive() throws IOException {
        when(databaseLocation.databaseLocation()).thenReturn("target_midas.db");

        sqliteSnapshot.create();

        Mockito.verify(createSnapshot).create();
        verify(archiveWriter).add(new File(TEST_SNAPSHOT_FILE), "target_midas.db");
    }

    @Test
    void closeCleansUpSnapshot() throws Exception {
        sqliteSnapshot.close();
        Mockito.verify(cleanupSnapshot).cleanup();
    }
}
