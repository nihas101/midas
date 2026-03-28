package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;

public interface BookingsReader {
    Booking systemGeneratedInterestForShareholderAndYear(final Integer shareholderId, final Integer year);

    Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Integer year);

    Bookings bookingsForShareholderAndYear(Integer shareholderId, Integer year);
}
