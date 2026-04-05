package de.nihas101.midas.backup.service.snapshot;

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
        final CreateSnapshotFile createSnapshotFile = new CreateSnapshotFile(
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
        final CreateSnapshotFile createSnapshotFile = new CreateSnapshotFile(
                null,
                null,
                null
        );

        Assertions.assertThrows(RuntimeException.class, createSnapshotFile::create);
    }

    @Test
    void createWithNullSnapshotFileName() {
        final CreateSnapshotFile createSnapshotFile = new CreateSnapshotFile(
                null,
                mock(JdbcTemplate.class),
                null
        );

        Assertions.assertThrows(RuntimeException.class, createSnapshotFile::create);
    }
}