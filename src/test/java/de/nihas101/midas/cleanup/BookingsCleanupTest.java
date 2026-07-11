package de.nihas101.midas.cleanup;

import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.config.MidasConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;

@ExtendWith(MockitoExtension.class)
class BookingsCleanupTest {

    @Mock
    private BookingsRepository bookingsRepository;

    @Test
    void defaultCleanUp() {
        final CleanupConfig config = new CleanupConfig();

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                LocalDate.now().minusYears(10),
                1000
        );
    }

    @Test
    void defaultCleanUpFromMidasConfig() {
        final MidasConfig config = new MidasConfig();

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                LocalDate.now().minusYears(10),
                1000
        );
    }

    @Test
    void cleanUpWithCustomCutoff() {
        final CleanupConfig config = new CleanupConfig();
        final Period cutoff = Period.ofDays(7);
        config.setCutoff(cutoff);

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                LocalDate.now().minus(cutoff),
                1000
        );
    }

    @Test
    void cleanUpWithoutLimit() {
        final CleanupConfig config = new CleanupConfig();
        config.setLimit(-1);

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBefore(LocalDate.now().minusYears(10));
    }
}