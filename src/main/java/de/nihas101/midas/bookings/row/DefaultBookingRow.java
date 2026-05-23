package de.nihas101.midas.bookings.row;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.money.MoneyAmount;

import java.util.List;

public record DefaultBookingRow(
        String displayId,
        String dateStr,
        String comment,
        MonthlyTotal amounts,
        MoneyAmount total,
        MoneyAmount balance,
        List<Booking> bookings
) implements BookingRow {

    @Override
    public MoneyAmount amount(final BookingType type) {
        return amounts.monthlyTotal(type);
    }

}
