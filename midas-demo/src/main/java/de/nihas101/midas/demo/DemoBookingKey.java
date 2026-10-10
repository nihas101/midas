package de.nihas101.midas.demo;

import de.nihas101.midas.commons.BookingType;

import java.time.LocalDate;

public record DemoBookingKey(
        int shareholderId,
        LocalDate bookingDate,
        BookingType bookingType
) {
}
