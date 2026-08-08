package de.nihas101.midas.persistance.sqlite;

import de.nihas101.midas.persistance.backup.ArchiveWriter;
import de.nihas101.midas.persistance.backup.CleanupSnapshot;
import de.nihas101.midas.persistance.backup.CreateSnapshot;
import de.nihas101.midas.persistance.backup.DatabaseLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

class SqliteSnapshotTest {

    private ArchiveWriter archiveWriter;
    private DatabaseLocation databaseLocation;
    private SqliteSnapshot sqliteSnapshot;
    private CleanupSnapshot cleanupSnapshot;
    private CreateSnapshot createSnapshot;
    private static final String TEST_SNAPSHOT_FILE = "test_snapshot.db";

    @BeforeEach
    void setUp() {
        archiveWriter = Mockito.mock(ArchiveWriter.class);
        databaseLocation = Mockito.mock(DatabaseLocation.class);
        cleanupSnapshot = Mockito.mock(CleanupSnapshot.class);
        createSnapshot = Mockito.mock(CreateSnapshot.class);
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
        Mockito.when(databaseLocation.databaseLocation()).thenReturn("target_midas.db");

        sqliteSnapshot.create();

        Mockito.verify(createSnapshot).create();
        Mockito.verify(archiveWriter).add(new File(TEST_SNAPSHOT_FILE), "target_midas.db");
    }

    @Test
    void closeCleansUpSnapshot() throws Exception {
        sqliteSnapshot.close();
        Mockito.verify(cleanupSnapshot).cleanup();
    }
}
