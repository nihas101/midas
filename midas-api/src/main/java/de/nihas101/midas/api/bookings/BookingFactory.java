package de.nihas101.midas.api.bookings;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

public interface BookingFactory {

    Booking create(
            final Integer shareholderId,
            final LocalDate date,
            final BookingType type,
            final MoneyAmount moneyAmount,
            final String comment,
            final Source source
    );

    default Booking createSystemGeneratedInterestBooking(
            final Integer shareholderId,
            final Year year,
            final MoneyAmount interest,
            final String comment
    ) {
        return create(
                shareholderId,
                year.atMonth(Month.DECEMBER).atEndOfMonth(),
                BookingType.INTEREST,
                interest,
                comment,
                Source.SYSTEM
        );
    }

    Booking create(final LocalDate now, final Source source);

    Booking create(final Integer shareholderId, final LocalDate date);
}
