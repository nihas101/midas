package de.nihas101.midas.persistance.sqlite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlitePragma {

    private final JdbcTemplate jdbcTemplate;

    // See: https://sqlite.org/lang_analyze.html#periodically_run_pragma_optimize_
    void optimizeAllTables() {
        log.info("Running PRAGMA optimize=0x10002");
        jdbcTemplate.execute("PRAGMA optimize=0x10002;");
    }

    void optimize() {
        log.info("Running PRAGMA optimize");
        jdbcTemplate.execute("PRAGMA optimize;");
    }
}