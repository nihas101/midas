package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;

import java.time.Year;

public interface BookingsReader {
    Booking systemGeneratedInterestForShareholderAndYear(final Integer shareholderId, final Year year);

    Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year);

    Bookings bookingsForShareholderAndYear(Integer shareholderId, Year year);

    boolean exists(Booking booking);
}
