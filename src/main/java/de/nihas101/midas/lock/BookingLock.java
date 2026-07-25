package de.nihas101.midas.lock;

import de.nihas101.midas.bookings.dto.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingLock {

    private final ShareholderLock delegate;

    public void assertUnlocked(final Booking booking) {
        delegate.assertUnlocked(
                booking != null
                        ? booking.getShareholderId()
                        : null,
                booking != null && booking.getDate() != null
                        ? Year.of(booking.getDate().getYear())
                        : null
        );
    }
}
