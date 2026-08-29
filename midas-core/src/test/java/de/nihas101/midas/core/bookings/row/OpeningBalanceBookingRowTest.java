package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpeningBalanceBookingRowTest {

    private static final Year TEST_YEAR = Year.now();

    @Mock
    private Bookings bookings;

    @Test
    void constructsFromBookingsCorrectly() {
        when(bookings.openingBalance()).thenReturn(new DefaultOpeningBalance(MoneyAmount.ofCents(1000L)));

        final OpeningBalanceBookingRow row = new OpeningBalanceBookingRow(bookings, "dd.MM.", TEST_YEAR);

        assertEquals("", row.displayId());
        assertEquals("01.01.", row.formattedDate());
        assertEquals("", row.comment());
        assertNotNull(row.amounts());
        assertEquals(MoneyAmount.ZERO, row.total());

        assertEquals(MoneyAmount.ofCents(1000L), row.balance());
        assertTrue(row.bookings().isEmpty());
    }

    @Test
    void constructsWithCustomDateFormat() {
        when(bookings.openingBalance()).thenReturn(new DefaultOpeningBalance(MoneyAmount.ofCents(1000L)));

        final OpeningBalanceBookingRow row = new OpeningBalanceBookingRow(bookings, "MM - dd", TEST_YEAR);

        assertEquals("01 - 01", row.formattedDate());
    }
}
