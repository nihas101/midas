package de.nihas101.midas.core.bookings.service;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.core.lock.BookingLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class LockingBookingsService implements BookingsWriter {

    private final BookingLock lock;
    private final BookingsService delegate;

    @Override
    public void create(final Booking booking) {
        lock.assertUnlocked(booking);
        delegate.create(booking);
    }

    @Override
    public void update(final Booking booking) {
        lock.assertUnlocked(booking);
        delegate.update(booking);
    }

    @Override
    public void delete(final Booking booking) {
        lock.assertUnlocked(booking);
        delegate.delete(booking);
    }

}
