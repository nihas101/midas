package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class DefaultBookingRow implements BookingRow {

    private final BaseBookingRow bookingRow;

    public DefaultBookingRow(
            final String displayId,
            final String dateStr,
            final String comment,
            final Map<BookingType, MoneyAmount> amounts,
            final MoneyAmount total,
            final MoneyAmount balance
    ) {
        this(
                new BaseBookingRow(
                        displayId,
                        dateStr,
                        comment,
                        amounts,
                        total,
                        balance
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
}
