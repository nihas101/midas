package de.nihas101.midas.core.bookings.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DefaultBookingFactory implements BookingFactory {

    @Override
    public Booking create(
            final Integer shareholderId,
            final LocalDate date,
            final BookingType type,
            final MoneyAmount moneyAmount,
            final String comment,
            final Source source
    ) {
        return new DefaultBooking(
                null,
                null,
                shareholderId,
                date,
                type,
                moneyAmount,
                comment,
                Source.SYSTEM
        );
    }

    @Override
    public Booking create(final LocalDate date, final Source source) {
        return new DefaultBooking(
                null,
                null,
                null,
                date,
                null,
                null,
                null,
                source
        );
    }

    @Override
    public Booking create(final Integer shareholderId, final LocalDate date) {
        return new DefaultBooking(
                null,
                null,
                shareholderId,
                date,
                null,
                null,
                null,
                Source.USER
        );
    }

}
