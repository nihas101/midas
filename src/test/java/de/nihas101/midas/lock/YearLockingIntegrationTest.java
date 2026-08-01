package de.nihas101.midas.lock;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.interest.service.InterestBookingsService;
import de.nihas101.midas.interest.service.bookingupdate.InterestUpdatingBookingsService;
import de.nihas101.midas.interest.service.openingbalanceupdate.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.lock.service.LockWriter;
import de.nihas101.midas.lock.service.LockedException;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class YearLockingIntegrationTest {

    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private LockWriter lockWriter;

    @Autowired
    private InterestUpdatingBookingsService bookingsService;

    @Autowired
    private InterestUpdatingOpeningBalanceService openingBalanceService;

    @Autowired
    private InterestBookingsService interestBookingsService;

    private Shareholder shareholder;

    @BeforeEach
    void setUp() {
        shareholdersService.create(new Shareholder(null, 999, "Lock", "Test"));
        shareholder = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Lock".equals(s.getFirstName()) && "Test".equals(s.getLastName()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void bookingMutationsInLockedYear_arePrevented() {
        final Year year2026 = Year.of(2026);
        lockWriter.lock(shareholder, year2026);

        final Booking bookingInLockedYear = new Booking(
                null,
                1,
                shareholder.getId(),
                LocalDate.of(2026, 6, 15),
                BookingType.WITHDRAWAL,
                MoneyAmount.of(new BigDecimal("100.00")),
                "Test withdrawal",
                Source.USER
        );

        // 1. Create booking in locked year throws LockedException
        Assertions.assertThrows(LockedException.class, () -> bookingsService.create(bookingInLockedYear));

        // 2. Update booking in locked year throws LockedException
        bookingInLockedYear.setId(123);
        Assertions.assertThrows(LockedException.class, () -> bookingsService.update(bookingInLockedYear));

        // 3. Delete booking in locked year throws LockedException
        Assertions.assertThrows(LockedException.class, () -> bookingsService.delete(bookingInLockedYear));
    }

    @Test
    void openingBalanceMutationsInLockedYear_arePrevented() {
        final Year year2026 = Year.of(2026);
        lockWriter.lock(shareholder, year2026);

        final OpeningBalance openingBalanceInLockedYear = OpeningBalance.builder()
                .shareholderId(shareholder.getId())
                .year(year2026)
                .openingBalance(MoneyAmount.of(new BigDecimal("500.00")))
                .source(Source.USER)
                .build();

        // 1. Create opening balance in locked year throws LockedException
        Assertions.assertThrows(LockedException.class, () -> openingBalanceService.create(openingBalanceInLockedYear));

        // 2. Update opening balance in locked year throws LockedException
        openingBalanceInLockedYear.setId(456);
        Assertions.assertThrows(LockedException.class, () -> openingBalanceService.update(openingBalanceInLockedYear));
    }

    @Test
    void carryForwardClosingBalanceToLockedNextYear_isPrevented() {
        final Year year2025 = Year.of(2025);
        final Year year2026 = Year.of(2026);

        // Year 2025 is UNLOCKED, Year 2026 is LOCKED
        lockWriter.lock(shareholder, year2026);

        // 1. Opening balance for 2025 (unlocked) works
        final OpeningBalance balance2025 = OpeningBalance.builder()
                .shareholderId(shareholder.getId())
                .year(year2025)
                .openingBalance(MoneyAmount.of(new BigDecimal("1000.00")))
                .source(Source.USER)
                .build();
        openingBalanceService.create(balance2025);

        // 2. Attempting to set/carry-forward opening balance to 2026 (locked year) throws LockedException
        final OpeningBalance carriedOverBalanceFor2026 = OpeningBalance.builder()
                .shareholderId(shareholder.getId())
                .year(year2026)
                .openingBalance(MoneyAmount.of(new BigDecimal("1200.00")))
                .source(Source.SYSTEM)
                .build();

        Assertions.assertThrows(LockedException.class, () -> openingBalanceService.create(carriedOverBalanceFor2026));

        carriedOverBalanceFor2026.setId(789);
        Assertions.assertThrows(LockedException.class, () -> openingBalanceService.update(carriedOverBalanceFor2026));
    }

    @Test
    void interestBookingMutationsInLockedYear_arePrevented() {
        final Year year2026 = Year.of(2026);
        lockWriter.lock(shareholder, year2026);

        final Booking interestBooking = new Booking(
                null,
                1,
                shareholder.getId(),
                LocalDate.of(2026, 12, 31),
                BookingType.INTEREST,
                MoneyAmount.of(new BigDecimal("50.00")),
                "System interest",
                Source.SYSTEM
        );

        Assertions.assertThrows(LockedException.class, () -> interestBookingsService.create(interestBooking));
        Assertions.assertThrows(LockedException.class, () -> interestBookingsService.deleteInterestBooking(shareholder, year2026));
    }
}
