package de.nihas101.midas.persistance.backup;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public interface DbSnapshotFactory {

    DbSnapshot create(
            final JdbcTemplate jdbcTemplate,
            final ArchiveWriter archiveWriter
    );
}
