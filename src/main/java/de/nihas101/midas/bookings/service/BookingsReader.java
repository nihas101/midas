package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Bookings;

public interface BookingsReader {
    Bookings bookingsForShareholderAndYear(Integer shareholderId, Integer year);
}
