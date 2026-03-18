package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultBookingRow implements BookingRow {

    private final BookingRow bookingRow;

    public DefaultBookingRow(
            final String displayId,
            final String dateStr,
            final String comment,
            final Map<BookingType, MoneyAmount> amounts,
            final MoneyAmount total,
            final MoneyAmount balance,
            final List<Booking> bookings
    ) {
        this(
                new BaseBookingRow(
                        displayId,
                        dateStr,
                        comment,
                        amounts,
                        total,
                        balance,
                        bookings
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
    public List<Booking> bookings() {
        return bookingRow.bookings();
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

}
