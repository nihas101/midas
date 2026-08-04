package de.nihas101.midas.core.lock;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.lock.LockReader;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.lock.service.LockedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Year;

@ExtendWith(MockitoExtension.class)
class BookingLockTest {

    @Mock
    private LockReader lock;

    @Test
    void assertUnlocked_unlocked() {
        final BookingLock bookingLock = new BookingLock(new ShareholderLock(lock));

        final Booking booking = new DefaultBooking(
                1,
                2,
                3,
                LocalDate.of(2026, 7, 25),
                BookingType.WITHDRAWAL,
                MoneyAmount.ZERO,
                "comment",
                Source.USER
        );
        Mockito.when(lock.isLocked(3, Year.of(2026)))
                .thenReturn(false);

        // Assertion passes without throwing
        bookingLock.assertUnlocked(booking);
    }

    @Test
    void assertUnlocked_locked() {
        final BookingLock bookingLock = new BookingLock(new ShareholderLock(lock));

        final Booking booking = new DefaultBooking(
                1,
                2,
                3,
                LocalDate.of(2026, 7, 25),
                BookingType.WITHDRAWAL,
                MoneyAmount.ZERO,
                "comment",
                Source.USER
        );
        Mockito.when(lock.isLocked(3, Year.of(2026)))
                .thenReturn(true);

        // Assertion passes without throwing
        Assertions.assertThrows(LockedException.class, () -> bookingLock.assertUnlocked(booking));
    }

    @Test
    void assertUnlocked_nullShareholderId() {
        final BookingLock bookingLock = new BookingLock(new ShareholderLock(lock));

        final Booking booking = new DefaultBooking(
                1,
                2,
                null,
                LocalDate.of(2026, 7, 25),
                BookingType.WITHDRAWAL,
                MoneyAmount.ZERO,
                "comment",
                Source.USER
        );

        // Assertion passes without throwing
        bookingLock.assertUnlocked(booking);
    }

    @Test
    void assertUnlocked_nullDate() {
        final BookingLock bookingLock = new BookingLock(new ShareholderLock(lock));

        final Booking booking = new DefaultBooking(
                1,
                2,
                3,
                null,
                BookingType.WITHDRAWAL,
                MoneyAmount.ZERO,
                "comment",
                Source.USER
        );

        // Assertion passes without throwing
        bookingLock.assertUnlocked(booking);
    }

    @Test
    void assertUnlocked_nullBooking() {
        final BookingLock bookingLock = new BookingLock(new ShareholderLock(lock));

        // Assertion passes without throwing
        bookingLock.assertUnlocked(null);
    }
}