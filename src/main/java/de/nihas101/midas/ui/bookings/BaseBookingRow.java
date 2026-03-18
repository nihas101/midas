package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

import java.util.List;
import java.util.Map;

public record BaseBookingRow(
        String displayId,
        String dateStr,
        String comment,
        Map<BookingType, MoneyAmount> amounts,
        MoneyAmount total,
        MoneyAmount balance,
        List<Booking> bookings
) implements BookingRow {

    @Override
    public List<Booking> bookings() {
        return bookings;
    }

    @Override
    public MoneyAmount amount(final BookingType type) {
        return amounts.getOrDefault(type, MoneyAmount.ZERO);
    }

}

