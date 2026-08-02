package de.nihas101.midas.core.bookings.service;

import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.dto.Bookings;

import java.time.LocalDate;
import java.time.Year;

public interface BookingsReader {
    Bookings bookingsForShareholderAndYear(final Integer shareholderId, final Year year);

    Bookings bookingsForShareholderAndDates(
            final Integer shareholderId,
            final LocalDate startDate,
            final LocalDate endDate
    );

    boolean exists(final Booking booking);
}
