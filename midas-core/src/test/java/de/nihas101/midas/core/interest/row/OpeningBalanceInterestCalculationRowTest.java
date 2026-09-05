package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.MoneyAmount;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpeningBalanceInterestCalculationRowTest {

    @Test
    void testRowProperties() {
        final Bookings bookings = mock(Bookings.class);
        final OpeningBalance openingBalance = mock(OpeningBalance.class);
        when(openingBalance.getOpeningBalance()).thenReturn(MoneyAmount.of(new BigDecimal("1000.00")));
        when(bookings.openingBalance()).thenReturn(openingBalance);

        final MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(eq("interest.opening-balance"), any(), eq(Locale.GERMAN)))
                .thenReturn("Anfangsbestand 2026");

        final OpeningBalanceInterestCalculationRow row = new OpeningBalanceInterestCalculationRow(
                bookings,
                Year.of(2026),
                new BigDecimal("5.0"),
                messageSource,
                Locale.GERMAN
        );

        assertEquals("Anfangsbestand 2026", row.label());
        assertNull(row.totalTransaction());
        assertEquals(MoneyAmount.of(new BigDecimal("1000.00")), row.balanceAtEndOfMonth().moneyAmount());
        assertEquals(new BigDecimal("30"), row.interestDaysCount());
    }

    @Test
    void customInterestDays() {
        final Bookings bookings = mock(Bookings.class);
        final OpeningBalance openingBalance = mock(OpeningBalance.class);
        when(openingBalance.getOpeningBalance()).thenReturn(MoneyAmount.of(new BigDecimal("2000.00")));
        when(bookings.openingBalance()).thenReturn(openingBalance);

        final MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(eq("interest.opening-balance"), any(), eq(Locale.GERMAN)))
                .thenReturn("Anfangsbestand 2026");

        final OpeningBalanceInterestCalculationRow row = new OpeningBalanceInterestCalculationRow(
                bookings,
                Year.of(2024),
                new BigDecimal("4.0"),
                new BigDecimal("15"),
                messageSource,
                Locale.GERMAN
        );

        assertEquals("Anfangsbestand 2026", row.label());
        assertEquals(new BigDecimal("15"), row.interestDaysCount());
    }
}
