package de.nihas101.midas.api.bookings;

import de.nihas101.midas.api.shareholder.Shareholder;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public interface BookingsReader {
    Bookings bookingsForShareholderAndYear(final Integer shareholderId, final Year year);

    Bookings bookingsForShareholderAndDates(
            final Integer shareholderId,
            final LocalDate startDate,
            final LocalDate endDate
    );

    boolean exists(final Booking booking);

    List<Year> yearsWithBookings(
            final Shareholder shareholder,
            final LocalDate start,
            final LocalDate end
    );
}
