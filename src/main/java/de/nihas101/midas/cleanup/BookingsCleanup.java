package de.nihas101.midas.cleanup;

import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.config.MidasConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingsCleanup {

    private final BookingsRepository bookingsRepository;
    private final CleanupConfig cleanupConfig;

    @Autowired
    public BookingsCleanup(
            final BookingsRepository bookingsRepository,
            final MidasConfig midasConfig
    ) {
        this(
                bookingsRepository,
                midasConfig.getCleanup()
        );
    }

    public void cleanUp() {
        final LocalDate cutoff = LocalDate.now().minus(cleanupConfig.getCutoff());
        final int limit = cleanupConfig.getLimit();

        final int deletedBookingsCount = deletedBookings(limit, cutoff);
        log.info("Removed {} bookings (limit: {}), because they were made before the cutoff ({})",
                deletedBookingsCount, limit, cutoff);
    }

    private int deletedBookings(final int limit, final LocalDate cutoff) {
        return limit > -1
                ? bookingsRepository.deleteBeforeWithLimit(cutoff, limit)
                : bookingsRepository.deleteBefore(cutoff);
    }
}
