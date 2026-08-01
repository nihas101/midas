package de.nihas101.midas.cleanup;

import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.lock.repository.LockRepository;
import de.nihas101.midas.lock.service.LockReader;
import de.nihas101.midas.lock.service.LockWriter;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingsCleanupLockedYearIT {

    @Autowired
    private ShareholdersRepository shareholdersRepository;

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private LockRepository lockRepository;

    @Autowired
    private LockWriter lockWriter;

    @Autowired
    private LockReader lockReader;

    @Test
    void cleanUp_worksEvenIfYearIsLocked() {
        // 1. Create a shareholder
        final ShareholderEntity shareholderEntity = shareholdersRepository.save(
                new ShareholderEntity(null, 888, "Cleanup", "Test")
        );
        final Shareholder shareholder = Shareholder.fromEntity(shareholderEntity);

        // 2.1. Lock the year 2014 for this shareholder
        final Year year2014 = Year.of(2014);
        lockWriter.lock(shareholder, year2014);

        // 2.2. Lock the year 2015 for this shareholder
        final Year year2015 = Year.of(2015);
        lockWriter.lock(shareholder, year2015);
        Assertions.assertTrue(lockReader.isLocked(shareholder, year2015), "Year 2015 should be locked before cleanup");

        // 3. Create a booking directly in the DB in year 2015 (before 10 year cutoff)
        final LocalDate oldDate = LocalDate.of(2015, 3, 10);
        final BookingEntity oldBooking = new BookingEntity(
                null,
                1,
                shareholderEntity,
                oldDate,
                BookingType.WITHDRAWAL,
                MoneyAmount.of(new BigDecimal("200.00")),
                "Old booking in locked year",
                Source.USER
        );
        bookingsRepository.save(oldBooking);

        Assertions.assertFalse(bookingsRepository.findAll().isEmpty(), "Old booking should exist before cleanup");

        // 4. Configure cleanup with cutoff of 5 years (which includes 2015)
        final CleanupConfig cleanupConfig = new CleanupConfig();
        cleanupConfig.setCutoff(Period.ofYears(5));
        cleanupConfig.setLimit(-1);

        final BookingsCleanup cleanup = new BookingsCleanup(bookingsRepository, lockRepository, cleanupConfig);

        // 5. Execute cleanup and verify it succeeds despite year 2015 being locked
        Assertions.assertDoesNotThrow(cleanup::cleanUp);

        // 6. Verify the booking was cleaned up
        Assertions.assertTrue(
                bookingsRepository.findAll().stream().noneMatch(b -> b.getDate().equals(oldDate)),
                "Old booking in locked year should have been cleaned up"
        );

        // 7. Verify the lock for year 2015 was not removed
        Assertions.assertFalse(
                lockReader.isLocked(shareholder, year2015),
                "Lock for year 2015 should have been removed after all bookings for that year were cleaned up"
        );

        // 8. Verify the lock for year 2014 was removed
        Assertions.assertFalse(
                lockReader.isLocked(shareholder, year2014),
                "Lock for year 2015 should have been removed after all bookings for that year were cleaned up"
        );
    }
}
