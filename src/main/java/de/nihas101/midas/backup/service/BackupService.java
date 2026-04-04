package de.nihas101.midas.backup.service;

import de.nihas101.midas.backup.service.snapshot.ApplicationPropertiesSnapshot;
import de.nihas101.midas.backup.service.snapshot.JarSnapshot;
import de.nihas101.midas.backup.service.snapshot.MidasSnapshot;
import de.nihas101.midas.backup.service.snapshot.SqliteDatabaseLocation;
import de.nihas101.midas.backup.service.snapshot.SqliteSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class BackupService { // TODO: Extract interface

    private final JdbcTemplate jdbcTemplate;
    private final BackupStatusWriter backupStatusWriter;
    private final MidasExecutableResolver executableResolver;
    private final String datasourceUrl;

    public BackupService(
            final JdbcTemplate jdbcTemplate,
            final BackupStatusWriter backupStatusWriter,
            final MidasExecutableResolver executableResolver,
            @Value("${spring.datasource.url}") final String datasourceUrl
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.backupStatusWriter = backupStatusWriter;
        this.executableResolver = executableResolver;
        this.datasourceUrl = datasourceUrl;
    }

    // TODO: Wrap byte[] in output class that abstracts where the return goes
    //  Even better -> Don't have a return and pass in the class that handles the output stream as needed
    public byte[] createBackup() throws Exception {
        log.info("Starting backup creation...");
        try (
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ZipOutputStream zos = new ZipOutputStream(baos);
                final MidasSnapshot midasSnapshot = midasSnapshot(zos)
        ) {
            midasSnapshot.create();
            zos.finish();
            final byte[] byteArray = baos.toByteArray();
            backupStatusWriter.updateLastSuccessAt(LocalDateTime.now());
            return byteArray;
        }
    }

    private MidasSnapshot midasSnapshot(final ZipOutputStream zos) {
        final SqliteDatabaseLocation databaseLocation = new SqliteDatabaseLocation(datasourceUrl);
        // TODO: Don't pass in the raw ZipOutputStream here, but instead wrap it
        return new MidasSnapshot(
                Set.of(
                        new SqliteSnapshot(
                                jdbcTemplate,
                                zos,
                                databaseLocation
                        ),
                        new ApplicationPropertiesSnapshot(
                                zos,
                                databaseLocation
                        ),
                        new JarSnapshot(zos, executableResolver)
                )
        );
    }

}
