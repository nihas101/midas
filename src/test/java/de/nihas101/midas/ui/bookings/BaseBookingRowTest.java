package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

class BaseBookingRowTest {

    @ParameterizedTest
    @EnumSource(BookingType.class)
    void amount(BookingType bookingType) {
        final Map<BookingType, MoneyAmount> amounts = new HashMap<>();
        for (final BookingType type : BookingType.values()) {
            amounts.put(type, MoneyAmount.ofCents((long) type.getId()));
        }

        final BaseBookingRow bookingRow = new BaseBookingRow(
                "1",
                "2026-01-01",
                "comment",
                amounts,
                MoneyAmount.ofCents(100L),
                MoneyAmount.ofCents(1000L)
        );
        final MoneyAmount amount = bookingRow.amount(bookingType);
        Assertions.assertEquals(bookingType.getId(), amount.getCents());
    }
}