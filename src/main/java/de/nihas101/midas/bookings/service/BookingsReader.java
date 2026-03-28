package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;

import java.time.Year;

public interface BookingsReader {
    Bookings bookingsForShareholderAndYear(Integer shareholderId, Year year);

    boolean exists(Booking booking);
}
