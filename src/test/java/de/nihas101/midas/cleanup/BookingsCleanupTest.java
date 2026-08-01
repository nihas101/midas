package de.nihas101.midas.cleanup;

import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.lock.repository.LockRepository;
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

    @Mock
    private LockRepository lockRepository;

    @Test
    void defaultCleanUp() {
        final CleanupConfig config = new CleanupConfig();

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, lockRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                LocalDate.now().minusYears(10),
                1000
        );
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                LocalDate.now().minusYears(10).getYear()
        );
    }

    @Test
    void defaultCleanUpFromMidasConfig() {
        final MidasConfig config = new MidasConfig();

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, lockRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                LocalDate.now().minusYears(10),
                1000
        );
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                LocalDate.now().minusYears(10).getYear()
        );
    }

    @Test
    void cleanUpWithCustomCutoff() {
        final CleanupConfig config = new CleanupConfig();
        final Period cutoff = Period.ofDays(7);
        config.setCutoff(cutoff);

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, lockRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                LocalDate.now().minus(cutoff),
                1000
        );
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                LocalDate.now().minus(cutoff).getYear()
        );
    }

    @Test
    void cleanUpWithoutLimit() {
        final CleanupConfig config = new CleanupConfig();
        config.setLimit(-1);

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, lockRepository, config);
        cleanup.cleanUp();

        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBefore(LocalDate.now().minusYears(10));
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                LocalDate.now().minusYears(10).getYear()
        );
    }
}