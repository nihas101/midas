package de.nihas101.midas.persistance.sqlite;

import de.nihas101.midas.persistance.sqlite.config.SqliteConfig;
import de.nihas101.midas.persistance.sqlite.config.Vacuum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqliteVacuum {

    private static final int STATUS_ROW_ID = 1;

    private final Clock clock;
    private final JdbcTemplate jdbcTemplate;
    private final Vacuum vacuum;

    @Autowired
    public SqliteVacuum(
            final JdbcTemplate jdbcTemplate,
            final SqliteConfig sqliteConfig
    ) {
        this(
                Clock.systemUTC(),
                jdbcTemplate,
                sqliteConfig.getOptimize().getVacuum()
        );
    }

    public void vacuum() {
        if (!vacuum.isEnabled()) {
            return;
        }
        final Instant now = Instant.now(clock);
        final Long lastVacuumAt = queryLastVacuumAt();

        if (isVacuumDue(lastVacuumAt, now)) {
            log.info("Running VACUUM");
            jdbcTemplate.execute("VACUUM;");
            updateLastVacuumAt(now.toEpochMilli());
        } else {
            log.debug("Skipping VACUUM, last optimizeAllTables was at epoch millis {}", lastVacuumAt);
        }
    }

    private boolean isVacuumDue(final Long lastVacuumAtEpochMillis, final Instant now) {
        if (lastVacuumAtEpochMillis == null) {
            return true;
        }

        final Instant lastVacuumInstant = Instant.ofEpochMilli(lastVacuumAtEpochMillis);
        return Duration.between(lastVacuumInstant, now).compareTo(vacuum.getInterval()) >= 0;
    }

    private Long queryLastVacuumAt() {
        try {
            return jdbcTemplate.query(
                    "SELECT last_vacuum_at FROM midas_sqlite_optimization_status WHERE id = ?",
                    rs -> rs.next() ? (Long) rs.getObject("last_vacuum_at") : null,
                    STATUS_ROW_ID
            );
        } catch (DataAccessException e) {
            log.warn("Could not query midas_sqlite_optimization_status: {}", e.getMessage());
            return null;
        }
    }

    private void updateLastVacuumAt(final long epochMillis) {
        try {
            jdbcTemplate.update(
                    """
                            INSERT INTO midas_sqlite_optimization_status (id, last_vacuum_at) VALUES (?, ?) \
                            ON CONFLICT DO UPDATE SET last_vacuum_at = ? WHERE id = ?""",
                    STATUS_ROW_ID,
                    epochMillis,
                    epochMillis,
                    STATUS_ROW_ID
            );
        } catch (DataAccessException e) {
            log.warn("Could not update last_vacuum_at in midas_sqlite_optimization_status: {}", e.getMessage());
        }
    }
}