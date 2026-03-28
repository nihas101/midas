package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;

import java.time.Year;

public interface BookingsReader {
    Bookings bookingsForShareholderAndYear(final Integer shareholderId, final Year year);

    boolean exists(final Booking booking);
}
