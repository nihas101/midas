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
        log.info("Running PRAGMA optimize=0x10002 (initial analysis)");
        jdbcTemplate.execute("PRAGMA optimize=0x10002;");
        running = true;
    }

    @Override
    public void stop() {
        log.info("Running PRAGMA optimize (shutdown optimization)");
        jdbcTemplate.execute("PRAGMA optimize;");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
