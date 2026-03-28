package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.shareholders.dto.Shareholder;

import java.time.Year;

public interface InterestBookingsReader {
    Booking systemGeneratedInterestForShareholderAndYear(final Shareholder shareholder, final Year year);

    Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year);
}
