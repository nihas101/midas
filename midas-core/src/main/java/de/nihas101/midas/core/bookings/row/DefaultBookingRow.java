package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.core.money.MoneyAmount;

import java.util.List;

public record DefaultBookingRow(
        String displayId,
        String dateStr,
        String comment,
        MonthlyTotal amounts,
        MoneyAmount total,
        Booking booking
) implements BookingRow {

    @Override
    public MoneyAmount amount(final BookingType type) {
        return amounts.monthlyTotal(type);
    }

    @Override
    public MoneyAmount balance() {
        return MoneyAmount.ZERO;
    }

    @Override
    public List<Booking> bookings() {
        return List.of(booking);
    }

}
