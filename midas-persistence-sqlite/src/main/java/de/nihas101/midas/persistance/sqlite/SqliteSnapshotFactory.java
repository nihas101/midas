package de.nihas101.midas.persistance.sqlite;

import de.nihas101.midas.persistance.backup.ArchiveWriter;
import de.nihas101.midas.persistance.backup.DatabaseLocationFactory;
import de.nihas101.midas.persistance.backup.DbSnapshot;
import de.nihas101.midas.persistance.backup.DbSnapshotFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "spring.datasource.driver-class-name",
        havingValue = "org.sqlite.JDBC"
)
public class SqliteSnapshotFactory implements DbSnapshotFactory {

    private final DatabaseLocationFactory databaseLocationFactory;

    public SqliteSnapshotFactory(
            final DatabaseLocationFactory databaseLocationFactory
    ) {
        this.databaseLocationFactory = databaseLocationFactory;
    }

    @Override
    public DbSnapshot create(
            final JdbcTemplate jdbcTemplate,
            final ArchiveWriter archiveWriter
    ) {
        return new SqliteSnapshot(
                jdbcTemplate,
                archiveWriter,
                databaseLocationFactory.create()
        );
    }
}
