package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class InitialBalanceRow implements IBookingRow {

    private final BookingRow bookingRow;

    public InitialBalanceRow(
            final String comment,
            final Bookings bookings
    ) {
        this(
                new BookingRow(
                        "",
                        "01.01.",
                        comment,
                        Map.of(BookingType.SALDOVORTRAG, bookings.initialBalance()),
                        MoneyAmount.ZERO,
                        bookings.initialBalance()
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
