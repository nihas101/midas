package de.nihas101.midas.core.backup.service.snapshot;

import de.nihas101.midas.persistance.backup.CleanupSnapshotFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;

class CreateSnapshotFileTest {

    @Test
    void create() {
        final CleanupSnapshotFile cleanupSnapshotFile = mock(CleanupSnapshotFile.class);
        final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        final String snapshotFilename = "backup.db";
        final de.nihas101.midas.persistance.backup.CreateSnapshotFile createSnapshotFile = new de.nihas101.midas.persistance.backup.CreateSnapshotFile(
                cleanupSnapshotFile,
                jdbcTemplate,
                snapshotFilename
        );

        createSnapshotFile.create();

        Mockito.verify(cleanupSnapshotFile).cleanup();
        Mockito.verify(jdbcTemplate).execute("VACUUM INTO '" + snapshotFilename + "'");
    }

    @Test
    void createWithNullJdbcTemplate() {
        final de.nihas101.midas.persistance.backup.CreateSnapshotFile createSnapshotFile = new de.nihas101.midas.persistance.backup.CreateSnapshotFile(
                null,
                null,
                null
        );

        Assertions.assertThrows(RuntimeException.class, createSnapshotFile::create);
    }

    @Test
    void createWithNullSnapshotFileName() {
        final de.nihas101.midas.persistance.backup.CreateSnapshotFile createSnapshotFile = new de.nihas101.midas.persistance.backup.CreateSnapshotFile(
                null,
                mock(JdbcTemplate.class),
                null
        );

        Assertions.assertThrows(RuntimeException.class, createSnapshotFile::create);
    }
}