package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.bookings.Bookings;

import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpeningBalanceInterestCalculationRowTest {

    @Mock
    private Bookings bookings;

    @Mock
    private MessageSource messageSource;

    @Test
    void constructsCorrectlyFromBookings() {
        when(bookings.openingBalance()).thenReturn(new DefaultOpeningBalance(MoneyAmount.ofCents(100000L)));
        when(messageSource.getMessage(eq("interest.opening-balance"), any(), eq(Locale.ENGLISH)))
                .thenReturn("Anfangsbestand 2026");

        final OpeningBalanceInterestCalculationRow row = new OpeningBalanceInterestCalculationRow(
                bookings,
                Year.of(2026),
                BigDecimal.valueOf(5),
                messageSource,
                Locale.ENGLISH
        );

        assertEquals("Anfangsbestand 2026", row.label());
        assertEquals(30, row.interestDaysCount());
    }
}
