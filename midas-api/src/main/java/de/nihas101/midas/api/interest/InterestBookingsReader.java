package de.nihas101.midas.api.interest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.shareholder.Shareholder;

import java.time.Year;

public interface InterestBookingsReader {

    Booking systemGeneratedInterestForShareholderAndYear(final Shareholder shareholder, final Year year);

    Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year);
}
