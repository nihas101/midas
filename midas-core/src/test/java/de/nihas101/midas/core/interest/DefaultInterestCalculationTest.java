package de.nihas101.midas.core.interest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultInterestCalculationTest {

    @Test
    void calculateWithoutBookings() {
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
    void calculateWithBookings() {
        final List<Booking> bookingList = List.of(
                new DefaultBooking(
                        1,
                        2,
                        3,
                        LocalDate.of(2026, Month.MARCH, 15),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(20000L),
                        "Test Withdrawal",
                        Source.SYSTEM
                )
        );

        final Bookings bookings = new DefaultBookings(
                bookingList,
                new DefaultOpeningBalance(MoneyAmount.ofCents(100000L))
        );

        final InterestCalculation calculation = new DefaultInterestCalculation(
                bookings,
                Year.of(2026),
                BigDecimal.valueOf(4.0)
        );

        assertNotNull(calculation.interestSum());
        assertTrue(calculation.divisor().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(calculation.interest());
        assertEquals(12, calculation.monthlyBalances().size());
    }

    @Test
    void calculateWithZeroInterestRate() {
        final Bookings bookings = new DefaultBookings(
                List.of(),
                new DefaultOpeningBalance(MoneyAmount.ofCents(100000L))
        );

        final InterestCalculation calculation = new DefaultInterestCalculation(
                bookings,
                Year.of(2026),
                BigDecimal.ZERO
        );

        assertEquals(BigDecimal.ZERO, calculation.divisor());
        assertEquals(MoneyAmount.ZERO, calculation.interest());
        assertEquals(MoneyAmount.ofCents(100000L), calculation.finalSum());
    }
}
