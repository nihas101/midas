package de.nihas101.midas.sqlite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "midas.sqlite.optimize.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class SqliteOptimizer implements SmartLifecycle {

    private final JdbcTemplate jdbcTemplate;
    private volatile boolean running = false;

    // See: https://sqlite.org/lang_analyze.html#periodically_run_pragma_optimize_
    @Override
    public void start() {
        log.info("Running PRAGMA optimize=0x10002");
        jdbcTemplate.execute("PRAGMA optimize=0x10002;");
        running = true;
    }

    @Override
    public void stop() {
        log.info("Running PRAGMA optimize");
        jdbcTemplate.execute("PRAGMA optimize;");

        // TODO: Only run vacuum when the last execution of vacuum is at least 30 days ago
        //log.info("Running VACUUM");
        //jdbcTemplate.execute("VACUUM");

        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
