package de.nihas101.midas.core.interest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.bookings.dto.DefaultBookings;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultInterestCalculationTest {

    @Test
    void interestCalculation_withZeroBookingsAndOpeningBalance() {
        final Bookings bookings = new DefaultBookings(
                List.of(),
                new DefaultOpeningBalance(MoneyAmount.ofCents(100000L))
        );

        final InterestCalculation calculation = new DefaultInterestCalculation(
                bookings,
                Year.of(2026),
                BigDecimal.valueOf(5.0)
        );

        assertNotNull(calculation.interestSum());
        assertNotNull(calculation.divisor());
        assertNotNull(calculation.interest());
        assertNotNull(calculation.finalSum());
        assertEquals(12, calculation.monthlyBalances().size());
        assertEquals(12, calculation.monthlyTotalSums().size());
        assertEquals(12, calculation.interests().size());
    }

    @Test
    void interestCalculation_withNullOpeningBalanceAndZeroInterestRate() {
        final Bookings bookings = mock(Bookings.class);
        when(bookings.openingBalance()).thenReturn(null);
        when(bookings.bookingsInMonth(any())).thenReturn(new FilteredBookings(List.of()));

        final DefaultInterestCalculation calculation = new DefaultInterestCalculation(
                bookings,
                Year.of(2026),
                BigDecimal.ZERO
        );

        assertEquals(MoneyAmount.ZERO, calculation.interest());
        assertEquals(MoneyAmount.ZERO, calculation.finalSum());
        assertEquals(BigDecimal.ZERO, calculation.divisor());
    }

    @Test
    void interestCalculation_withBookings() {
        final Bookings bookings = mock(Bookings.class);
        final OpeningBalance openingBalance = mock(OpeningBalance.class);
        when(openingBalance.getOpeningBalance()).thenReturn(MoneyAmount.ofCents(100000L)); // 1000.00
        when(bookings.openingBalance()).thenReturn(openingBalance);

        final Booking bookingJan = DefaultBooking.builder()
                .id(1)
                .date(LocalDate.of(2026, Month.JANUARY, 15))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmount.ofCents(-10000L))
                .build();

        when(bookings.bookingsInMonth(Month.JANUARY)).thenReturn(new FilteredBookings(List.of(bookingJan)));
        for (Month month : Month.values()) {
            if (month != Month.JANUARY) {
                when(bookings.bookingsInMonth(month)).thenReturn(new FilteredBookings(List.of()));
            }
        }

        final DefaultInterestCalculation calculation = new DefaultInterestCalculation(
                bookings,
                Year.of(2026),
                BigDecimal.valueOf(3.5)
        );

        assertNotNull(calculation.interest());
        assertNotNull(calculation.finalSum());
    }
}
