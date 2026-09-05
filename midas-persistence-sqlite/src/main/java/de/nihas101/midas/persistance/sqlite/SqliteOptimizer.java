package de.nihas101.midas.persistance.sqlite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "midas.db.sqlite.optimize.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class SqliteOptimizer implements SmartLifecycle {

    private final SqlitePragma sqlitePragma;
    private final SqliteVacuum sqliteVacuum;
    private volatile boolean running = false;

    @Override
    public void start() {
        sqlitePragma.optimizeAllTables();
        running = true;
    }

    @Override
    public void stop() {
        sqlitePragma.optimize();
        sqliteVacuum.vacuum();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}

