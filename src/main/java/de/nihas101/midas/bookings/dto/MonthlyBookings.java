package de.nihas101.midas.bookings.dto;

import java.util.List;

/**
 * @param bookings TODO: Don't expose this
 */
public record MonthlyBookings(List<Booking> bookings) {
}
