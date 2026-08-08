package de.nihas101.midas.core.backup.service;

import de.nihas101.midas.api.backup.BackupService;
import de.nihas101.midas.api.backup.BackupStatusWriter;
import de.nihas101.midas.core.backup.service.snapshot.ApplicationPropertiesSnapshot;
import de.nihas101.midas.core.backup.service.snapshot.JarSnapshot;
import de.nihas101.midas.core.backup.service.snapshot.MidasSnapshot;
import de.nihas101.midas.core.backup.service.snapshot.ZipArchive;
import de.nihas101.midas.persistance.backup.DatabaseLocation;
import de.nihas101.midas.persistance.backup.DatabaseLocationFactory;
import de.nihas101.midas.persistance.backup.DbSnapshotFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class DefaultBackupService implements BackupService {

    private final JdbcTemplate jdbcTemplate;
    private final BackupStatusWriter backupStatusWriter;
    private final MidasExecutableResolver executableResolver;
    private final MidasTemplatesResolver templatesResolver;
    private final DatabaseLocationFactory databaseLocationFactory;
    private final DbSnapshotFactory dbSnapshotFactory;

    public DefaultBackupService(
            final JdbcTemplate jdbcTemplate,
            final BackupStatusWriter backupStatusWriter,
            final MidasExecutableResolver executableResolver,
            final MidasTemplatesResolver templatesResolver,
            final DatabaseLocationFactory databaseLocationFactory,
            final DbSnapshotFactory dbSnapshotFactory
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.backupStatusWriter = backupStatusWriter;
        this.executableResolver = executableResolver;
        this.templatesResolver = templatesResolver;
        this.databaseLocationFactory = databaseLocationFactory;
        this.dbSnapshotFactory = dbSnapshotFactory;
    }

    @Override
    public byte[] createBackup() throws Exception {
        log.info("Starting backup creation...");
        try (
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ZipOutputStream zos = new ZipOutputStream(baos);
                final ZipArchive zipArchive = new ZipArchive(baos, zos);
                final MidasSnapshot midasSnapshot = midasSnapshot(zipArchive)
        ) {
            midasSnapshot.create();
            final byte[] byteArray = zipArchive.byteArray();
            backupStatusWriter.updateLastSuccessAt(LocalDateTime.now());
            return byteArray;
        }
    }

    private MidasSnapshot midasSnapshot(final ZipArchive zipArchive) {
        final DatabaseLocation databaseLocation = databaseLocationFactory.create();
        return new MidasSnapshot(
                dbSnapshotFactory.create(
                        jdbcTemplate,
                        zipArchive
                ),
                new ApplicationPropertiesSnapshot(
                        zipArchive,
                        databaseLocation,
                        templatesResolver
                ),
                new JarSnapshot(
                        zipArchive,
                        executableResolver
                ),
                new TemplatesSnapshot(
                        zipArchive,
                        templatesResolver
                )
        );
    }

}
