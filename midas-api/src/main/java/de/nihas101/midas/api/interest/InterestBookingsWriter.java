package de.nihas101.midas.api.interest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.shareholder.Shareholder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

public interface InterestBookingsWriter {
    @Transactional
    void create(final Booking booking);

    @Transactional
    void update(final Booking booking);

    @Transactional
    void deleteInterestBooking(final Shareholder shareholder, final Year year);
}
