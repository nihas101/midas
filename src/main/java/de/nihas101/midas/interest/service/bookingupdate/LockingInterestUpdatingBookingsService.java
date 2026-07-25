package de.nihas101.midas.interest.service.bookingupdate;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.lock.BookingLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class LockingInterestUpdatingBookingsService implements InterestUpdatingBookingsService {

    private final BookingLock lock;
    private final DefaultInterestUpdatingBookingsService delegate;

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
