package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestBookingsService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.lock.BookingLock;
import de.nihas101.midas.core.lock.ShareholderLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@Primary
@RequiredArgsConstructor
public class LockingInterestRateService implements InterestBookingsService {

    private final BookingLock bookingLock;
    private final ShareholderLock shareholderLock;
    private final DefaultInterestBookingsService delegate;

    @Override
    public Booking systemGeneratedInterestForShareholderAndYear(final Shareholder shareholder, final Year year) {
        return delegate.systemGeneratedInterestForShareholderAndYear(shareholder, year);
    }

    @Override
    public Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year) {
        return delegate.interestRelatedBookingsForShareholderAndYear(shareholderId, year);
    }

    @Override
    public void create(final Booking booking) {
        bookingLock.assertUnlocked(booking);
        delegate.create(booking);
    }

    @Override
    public void update(final Booking booking) {
        bookingLock.assertUnlocked(booking);
        delegate.update(booking);
    }

    @Override
    public void deleteInterestBooking(final Shareholder shareholder, final Year year) {
        shareholderLock.assertUnlocked(shareholder, year);
        delegate.deleteInterestBooking(shareholder, year);
    }
}
