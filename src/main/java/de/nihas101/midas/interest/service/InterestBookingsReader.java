package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;

import java.time.Year;

public interface InterestBookingsReader {
    Booking systemGeneratedInterestForShareholderAndYear(final Integer shareholderId, final Year year);

    Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year);
}
