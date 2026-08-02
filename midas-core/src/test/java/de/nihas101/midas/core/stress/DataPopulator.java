package de.nihas101.midas.core.stress;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Populates the test database with stress-test data:
 * - 100 shareholders (identified by the "Stress_" first-name prefix)
 * - 1 000 bookings per shareholder per year
 * - 5 years of history  (current year − 4 ... current year)
 * => ~500 000 booking rows in total
 * <p>
 * The populator is idempotent: if the 100 stress-test shareholders already
 * exist it exits immediately without writing anything.
 * <p>
 * Run with:
 * mvn test -Dtest=DataPopulator -Dspring.profiles.active=stress
 */
@Slf4j
@SpringBootTest
@EnabledIf(expression = "#{environment.acceptsProfiles('stress')}", reason = "Only run when 'stress' profile is active")
public class DataPopulator {

    private static final String STRESS_PREFIX = "Stress_";
    private static final int NUM_SHAREHOLDERS = 100;
    private static final int BOOKINGS_PER_YEAR = 1_000;
    private static final int NUM_YEARS = 5;
    private static final int CURRENT_YEAR = LocalDate.now().getYear();

    /**
     * BookingType IDs as defined in {@code BookingType}.
     */
    private static final int[] BOOKING_TYPE_IDS = {1, 2, 3, 5}; // WITHDRAWAL, TAX_PREV_YEAR, TAX_CREDIT, COMPENSATION

    private static final Random RNG = new Random(42);

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void populate() {
        final int existingCount = countExistingStressShareholders();
        if (existingCount >= NUM_SHAREHOLDERS) {
            log.info("Stress-test data already present ({} shareholders found). Skipping population.", existingCount);
            return;
        }

        log.info("Starting stress-test data population…");
        final long start = System.currentTimeMillis();

        final List<Integer> shareholderIds = insertShareholders();
        insertBookings(shareholderIds);

        final long elapsed = System.currentTimeMillis() - start;
        log.info("Population complete: {} shareholders, ~{} bookings in {}ms",
                shareholderIds.size(),
                (long) shareholderIds.size() * BOOKINGS_PER_YEAR * NUM_YEARS,
                elapsed);
    }

    private int countExistingStressShareholders() {
        final Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM shareholders WHERE first_name LIKE ?",
                Integer.class,
                STRESS_PREFIX + "%"
        );
        return count == null ? 0 : count;
    }

    private List<Integer> insertShareholders() {
        log.info("Inserting {} shareholders…", NUM_SHAREHOLDERS);

        final List<Object[]> batch = new ArrayList<>(NUM_SHAREHOLDERS);
        for (int i = 1; i <= NUM_SHAREHOLDERS; i++) {
            batch.add(new Object[]{STRESS_PREFIX + "First" + i, "Last" + i});
        }
        jdbc.batchUpdate("INSERT INTO shareholders (first_name, last_name) VALUES (?, ?)", batch);

        // Retrieve the IDs that were just created
        return jdbc.queryForList(
                "SELECT id FROM shareholders WHERE first_name LIKE ? ORDER BY id",
                Integer.class,
                STRESS_PREFIX + "%"
        );
    }

    private void insertBookings(final List<Integer> shareholderIds) {
        final long totalBookings = (long) shareholderIds.size() * BOOKINGS_PER_YEAR * NUM_YEARS;
        log.info("Inserting {} bookings via batched JDBC…", totalBookings);

        // We flush in chunks to keep memory usage bounded
        final int CHUNK_SIZE = 10_000;
        final List<Object[]> chunk = new ArrayList<>(CHUNK_SIZE);

        for (final int shareholderId : shareholderIds) {
            for (int yearOffset = 0; yearOffset < NUM_YEARS; yearOffset++) {
                final int year = CURRENT_YEAR - (NUM_YEARS - 1 - yearOffset);

                for (int b = 0; b < BOOKINGS_PER_YEAR; b++) {
                    chunk.add(buildBookingRow(shareholderId, year, b));

                    if (chunk.size() >= CHUNK_SIZE) {
                        flushChunk(chunk);
                    }
                }
            }
        }

        if (!chunk.isEmpty()) {
            flushChunk(chunk);
        }

        log.info("All bookings inserted.");
    }

    /**
     * Returns an Object[] suitable for the INSERT below.
     * Columns: shareholder_id, date, type, amount (cents), comment, source
     */
    private Object[] buildBookingRow(final int shareholderId, final int year, final int index) {
        // Spread bookings evenly across the year: one per calendar day (approx.)
        final int dayOfYear = (index % 365) + 1;
        final LocalDate date = LocalDate.ofYearDay(year, dayOfYear);

        final int typeId = BOOKING_TYPE_IDS[index % BOOKING_TYPE_IDS.length];
        final long cents = (long) (RNG.nextInt(9_900) + 100) * 100L; // €1.00 – €99.00 in cents

        return new Object[]{
                shareholderId,
                date.toString(), // SQLite stores dates as TEXT / ISO-8601
                typeId,
                cents,
                "Stress #" + index,
                "USER"
        };
    }

    private void flushChunk(final List<Object[]> chunk) {
        jdbc.batchUpdate(
                "INSERT INTO bookings (shareholder_id, date, type, amount, comment, source) VALUES (?, ?, ?, ?, ?, ?)",
                chunk
        );
        log.info("Flushed chunk of {} bookings.", chunk.size());
        chunk.clear();
    }
}
