package de.nihas101.midas.persistance.sqlite;

import de.nihas101.midas.persistance.sqlite.config.Vacuum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SqliteOptimizerTest {

    private static final Instant NOW = Instant.parse("2026-09-05T12:00:00Z");

    private JdbcTemplate jdbcTemplate;
    private Clock fixedClock;
    private SqlitePragma sqlitePragma;
    private SqliteVacuum sqliteVacuum;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);

        sqlitePragma = new SqlitePragma(jdbcTemplate);
        sqliteVacuum = new SqliteVacuum(
                fixedClock,
                jdbcTemplate,
                new Vacuum(true, Duration.ofDays(30))
        );
    }

    @Test
    void start_runsPragmaOptimize() {
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);

        optimizer.start();

        verify(jdbcTemplate).execute("PRAGMA optimize=0x10002;");
        assertTrue(optimizer.isRunning());
    }

    @Test
    void stop_runsPragmaOptimize() {
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);
        optimizer.start();

        mockLastVacuumAt(NOW.minus(Duration.ofDays(31)).toEpochMilli());

        optimizer.stop();

        verify(jdbcTemplate).execute("PRAGMA optimize;");
        assertFalse(optimizer.isRunning());
    }

    @Test
    void stop_whenLastVacuumIsNull_runsVacuum() {
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);
        optimizer.start();

        mockLastVacuumAt(null);

        optimizer.stop();

        verify(jdbcTemplate).execute("VACUUM;");
        verifyTimestampUpdated();
    }

    @Test
    void stop_whenLastVacuumIsMoreThan30DaysAgo_runsVacuum() {
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);
        optimizer.start();

        mockLastVacuumAt(NOW.minus(Duration.ofDays(31)).toEpochMilli());

        optimizer.stop();

        verify(jdbcTemplate).execute("VACUUM;");
        verifyTimestampUpdated();
    }

    @Test
    void stop_whenLastVacuumIsExactly30DaysAgo_runsVacuum() {
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);
        optimizer.start();

        mockLastVacuumAt(NOW.minus(Duration.ofDays(30)).toEpochMilli());

        optimizer.stop();

        verify(jdbcTemplate).execute("VACUUM;");
    }

    @Test
    void stop_whenLastVacuumIsLessThan30DaysAgo_skipsVacuum() {
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);
        optimizer.start();

        mockLastVacuumAt(NOW.minus(Duration.ofDays(10)).toEpochMilli());

        optimizer.stop();

        verify(jdbcTemplate, never()).execute("VACUUM;");
    }

    @Test
    void stop_whenVacuumDisabled_neverRunsVacuum() {
        final Vacuum vacuumConfig = new Vacuum(false, Duration.ofDays(30));
        final SqliteVacuum sqliteVacuum = new SqliteVacuum(fixedClock, jdbcTemplate, vacuumConfig);
        final SqliteOptimizer optimizer = new SqliteOptimizer(sqlitePragma, sqliteVacuum);

        optimizer.start();
        optimizer.stop();

        verify(jdbcTemplate, never()).execute("VACUUM;");
    }

    @SuppressWarnings("unchecked")
    private void mockLastVacuumAt(final Long value) {
        when(jdbcTemplate.query(
                eq("SELECT last_vacuum_at FROM midas_sqlite_optimization_status WHERE id = ?"),
                any(ResultSetExtractor.class),
                eq(1)
        )).thenReturn(value);

        when(jdbcTemplate.update(
                eq("UPDATE midas_sqlite_optimization_status SET last_vacuum_at = ? WHERE id = ?"),
                any(Long.class),
                eq(1)
        )).thenReturn(1);
    }

    private void verifyTimestampUpdated() {
        final long timestamp = NOW.toEpochMilli();
        final int rowId = 1;

        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO midas_sqlite_optimization_status (id, last_vacuum_at) VALUES (?, ?)" +
                        " ON CONFLICT DO UPDATE SET last_vacuum_at = ? WHERE id = ?"),
                eq(rowId),
                eq(timestamp),
                eq(timestamp),
                eq(rowId)
        );
    }
}
