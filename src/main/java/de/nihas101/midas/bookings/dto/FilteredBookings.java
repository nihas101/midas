package de.nihas101.midas.bookings.dto;

import java.util.List;

/**
 * @param bookings TODO: Don't expose this
 */
public record FilteredBookings(List<Booking> bookings) {
}
