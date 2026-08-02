package de.nihas101.midas.api.bookings;

import org.springframework.transaction.annotation.Transactional;

public interface BookingsWriter {
    @Transactional
    void create(final Booking booking);

    @Transactional
    void update(final Booking booking);

    @Transactional
    void delete(final Booking booking);
}
