package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpeningBalanceBookingRowTest {

    @Mock
    private Bookings bookings;

    @Test
    void constructsFromBookingsCorrectly() {
        when(bookings.openingBalance()).thenReturn(new DefaultOpeningBalance(MoneyAmount.ofCents(1000L)));

        final OpeningBalanceBookingRow row = new OpeningBalanceBookingRow(bookings);

        assertEquals("", row.displayId());
        assertEquals("01.01.", row.dateStr());
        assertEquals("", row.comment());
        assertNotNull(row.amounts());
        assertEquals(MoneyAmount.ZERO, row.total());

        assertEquals(MoneyAmount.ofCents(1000L), row.balance());
        assertTrue(row.bookings().isEmpty());
    }
}
