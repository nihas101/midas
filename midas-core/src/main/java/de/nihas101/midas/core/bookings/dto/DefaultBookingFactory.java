package de.nihas101.midas.core.bookings.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultBookingFactory implements BookingFactory {

    @Override
    public Booking create() {
        return new DefaultBooking();
    }
}
