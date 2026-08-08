package de.nihas101.midas.api.bookings;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;

import java.time.LocalDate;

public interface BookingFactory {

    Booking create(
            final Integer shareholderId,
            final LocalDate date,
            final BookingType type,
            final MoneyAmount moneyAmount,
            final String comment,
            final Source source
    );

    Booking create(final LocalDate now, final Source source);

    Booking create(final Integer shareholderId, final LocalDate date);
}
