package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OpeningBalanceBookingRow implements BookingRow { // TODO: Test

    private final BookingRow bookingRow;

    public OpeningBalanceBookingRow(final Bookings bookings) {
        this(
                new BaseBookingRow(
                        "",
                        "01.01.",
                        "",
                        Collections.emptyMap(),
                        MoneyAmount.ZERO,
                        bookings.openingBalance(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    public MoneyAmount amount(final BookingType type) {
        return bookingRow.amount(type);
    }

    @Override
    public String displayId() {
        return bookingRow.displayId();
    }

    @Override
    public String dateStr() {
        return bookingRow.dateStr();
    }

    @Override
    public String comment() {
        return bookingRow.comment();
    }

    @Override
    public Map<BookingType, MoneyAmount> amounts() {
        return bookingRow.amounts();
    }

    @Override
    public MoneyAmount total() {
        return bookingRow.total();
    }

    @Override
    public MoneyAmount balance() {
        return bookingRow.balance();
    }

    @Override
    public List<Booking> bookings() {
        return bookingRow.bookings();
    }

    @Override
    public String partName() {
        return "month-content";
    }
}
