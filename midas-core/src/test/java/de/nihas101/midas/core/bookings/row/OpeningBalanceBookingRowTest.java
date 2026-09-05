package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpeningBalanceBookingRowTest {

    @Test
    void testRowProperties() {
        final Bookings bookings = mock(Bookings.class);
        final OpeningBalance openingBalance = mock(OpeningBalance.class);
        when(openingBalance.getOpeningBalance()).thenReturn(MoneyAmount.ofCents(50000L));
        when(bookings.openingBalance()).thenReturn(openingBalance);

        final OpeningBalanceBookingRow row = new OpeningBalanceBookingRow(
                bookings,
                "dd.MM.yyyy",
                Year.of(2026)
        );

        assertEquals(MoneyAmount.ofCents(50000L), row.balance());
        assertEquals("01.01.2026", row.formattedDate());
        assertEquals("", row.displayId());
        assertEquals("", row.comment());
        assertEquals(MoneyAmount.ZERO, row.total());
        assertEquals(DefaultMonthlyTotalSum.ZERO, row.amounts());
        assertEquals(MoneyAmount.ZERO, row.amount(BookingType.WITHDRAWAL));
        assertEquals(MoneyAmount.ZERO, row.amount(BookingType.INTEREST));
        assertTrue(row.bookings().isEmpty());
    }
}
