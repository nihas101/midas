package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.shareholders.dto.Shareholder;
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
