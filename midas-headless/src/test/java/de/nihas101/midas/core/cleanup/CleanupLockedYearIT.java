package de.nihas101.midas.core.cleanup;

import de.nihas101.midas.api.lock.LockReader;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.config.DatesConfig;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ShareholderConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.config.TitleConfig;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.persistance.DbConfig;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverridesRepository;
import de.nihas101.midas.persistance.bookings.BookingEntity;
import de.nihas101.midas.persistance.bookings.BookingsRepository;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.lock.LockRepository;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CleanupLockedYearIT {

    @Autowired
    private ShareholdersRepository shareholdersRepository;

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private LockRepository lockRepository;

    @Autowired
    private AccountStatementOrdersRepository accountStatementOrdersRepository;

    @Autowired
    private AccountStatementOverridesRepository accountStatementOverridesRepository;

    @Autowired
    private InterestRateRepository interestRateRepository;

    @Autowired
    private OpeningBalanceRepository openingBalanceRepository;

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
        final Shareholder shareholder = DefaultShareholder.fromEntity(shareholderEntity);

        // 2.1. Lock the year seven years ago for this shareholder (a year that cannot be edited anymore)
        final Year sevenYearsAgo = Year.now().minusYears(7);
        lockWriter.lock(shareholder, sevenYearsAgo);

        // 2.2. Lock the year five years ago for this shareholder
        final Year fiveYearsAgo = Year.now().minusYears(5);
        lockWriter.lock(shareholder, fiveYearsAgo);
        assertTrue(lockReader.isLocked(shareholder, fiveYearsAgo), "The year 5 years ago should be locked before cleanup");
        assertTrue(lockReader.isLocked(shareholder, sevenYearsAgo), "The year 6 years ago should be locked before cleanup");

        // 3. Create a booking directly in the DB five years ago
        final LocalDate oldDate = LocalDate.of(fiveYearsAgo.getValue(), 3, 10);
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

        assertFalse(bookingsRepository.findAll().isEmpty(), "Old booking should exist before cleanup");

        // 4. Configure cleanup with cutoff of 5 years
        final CoreConfig config = createConfig();
        config.getCleanup().setCutoff(Period.ofYears(5));
        config.getCleanup().setLimit(-1);

        final Cleanup cleanup = new Cleanup(
                bookingsRepository,
                lockRepository,
                accountStatementOrdersRepository,
                accountStatementOverridesRepository,
                interestRateRepository,
                openingBalanceRepository,
                config
        );

        // 5. Execute cleanup and verify it succeeds despite the year being locked
        assertDoesNotThrow(cleanup::cleanUp);

        // 6. Verify the booking was cleaned up
        assertTrue(
                bookingsRepository.findAll().stream().noneMatch(b -> b.getDate().equals(oldDate)),
                "Old booking in locked year should have been cleaned up"
        );

        // 7. Verify the lock for five years ago was not removed
        assertTrue(
                lockReader.isLocked(shareholder, fiveYearsAgo),
                "Lock five years ago should stay"
        );

        // 8. Verify the lock for seven years ago was removed
        assertFalse(
                lockReader.isLocked(shareholder, sevenYearsAgo),
                "Lock six years ago should be removed"
        );
    }

    private static CoreConfig createConfig() {
        return new CoreConfig(
                new TitleConfig(),
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                new CleanupConfig(),
                new DbConfig() {
                },
                new DatesConfig(),
                new ShareholderConfig()
        );
    }
}
