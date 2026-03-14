package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;

import java.util.Map;

public record BaseBookingRow(
        String displayId,
        String dateStr,
        String comment,
        Map<BookingType, MoneyAmount> amounts,
        MoneyAmount total,
        MoneyAmount balance
) implements BookingRow {

    @Override
    public MoneyAmount amount(final BookingType type) {
        return amounts.getOrDefault(type, MoneyAmount.ZERO);
    }

}

