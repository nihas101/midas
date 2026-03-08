package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import org.springframework.transaction.annotation.Transactional;

public interface BookingsWriter {
    @Transactional
    void create(Booking booking);

    @Transactional
    void update(Booking booking);
}
