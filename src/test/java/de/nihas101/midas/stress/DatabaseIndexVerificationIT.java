package de.nihas101.midas.stress;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.util.List;

@Slf4j
@SpringBootTest
public class DatabaseIndexVerificationIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void verifyIndicesAreUsed() {
        // Verify index usage on the bookings table
        verifyIndex(
                "Bookings Filter",
                "SELECT * FROM bookings WHERE shareholder_id = ? AND date BETWEEN ? AND ?",
                new Object[]{1, "2026-01-01", "2026-12-31"},
                "idx_bookings_shareholder_date"
        );

        verifyIndex(
                "Bookings Grouping (Account Statement)",
                "SELECT MAX(id) as id, date, type, SUM(amount) as amount FROM bookings WHERE shareholder_id = ? AND date BETWEEN ? AND ? GROUP BY type",
                new Object[]{1, "2026-01-01", "2026-12-31"},
                "idx_bookings_shareholder_date"
        );

        // Verify index usage on the interest_rates table
        verifyIndex(
                "Interest Rates Lookup",
                "SELECT * FROM interest_rates WHERE shareholder_id = ? AND date = ?",
                new Object[]{1, "2026-01-01"},
                "idx_interest_rates_shareholder_date"
        );

        // Verify index usage on the opening_balances table
        verifyIndex(
                "Opening Balances Lookup",
                "SELECT * FROM opening_balances WHERE shareholder_id = ? AND date = ?",
                new Object[]{1, "2026-01-01"},
                "idx_opening_balances_shareholder_date"
        );

        // Verify index usage on the account_statement_overrides table
        verifyIndex(
                "Account Statement Overrides Lookup",
                "SELECT * FROM account_statement_overrides WHERE shareholder_id = ? AND year = ?",
                new Object[]{1, 2026},
                "idx_as_overrides_shareholder_year"
        );

        // Verify index usage on the account_statement_orders table
        verifyIndex(
                "Account Statement Orders Lookup",
                "SELECT * FROM account_statement_orders WHERE shareholder_id = ? AND year = ?",
                new Object[]{1, 2026},
                "idx_as_orders_shareholder_year"
        );
    }

    private void verifyIndex(String queryName, String sql, Object[] params, String expectedIndex) {
        log.info("--------------------------------------------------");
        log.info("Verifying Index usage for: {}", queryName);
        log.info("SQL: {}", sql);

        // 1. Fetch query plan details
        List<String> details = jdbc.query("EXPLAIN QUERY PLAN " + sql,
                (rs, rowNum) -> rs.getString("detail"),
                params);

        log.info("Query Plan Details:");
        details.forEach(d -> log.info("  -> {}", d));

        boolean usesIndex = details.stream().anyMatch(d -> d.contains(expectedIndex));
        Assertions.assertTrue(usesIndex, String.format(
                "Query '%s' should use index '%s'. Actual plan details: %s",
                queryName, expectedIndex, details));

        // 2. Measure execution time
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        jdbc.queryForList(sql, params);
        stopWatch.stop();

        log.info("Execution time: {} ms ({} ns)", stopWatch.getTotalTimeMillis(), stopWatch.getTotalTimeNanos());
        log.info("--------------------------------------------------");
    }
}
