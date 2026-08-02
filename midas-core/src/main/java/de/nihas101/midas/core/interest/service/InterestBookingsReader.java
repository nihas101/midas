package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.dto.Bookings;
import de.nihas101.midas.core.shareholders.dto.Shareholder;

import java.time.Year;

public interface InterestBookingsReader {
    Booking systemGeneratedInterestForShareholderAndYear(final Shareholder shareholder, final Year year);

    Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year);
}
