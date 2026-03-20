package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalSumCalculatorTestTotal {

    @ParameterizedTest
    @EnumSource(Month.class)
    void monthlyTotals_sumsAmountsByType(Month testMonth) {
        // Arrange
        Booking b1 = createBooking(BookingType.WITHDRAWAL, 1000L, testMonth, "A");
        Booking b2 = createBooking(BookingType.WITHDRAWAL, 500L, testMonth, "B");
        Booking b3 = createBooking(BookingType.INTEREST, 200L, testMonth, "C");

        MonthlyTotalSum.MonthlySumTotalCalculator calculator = createCalculator(new MonthlyBookings(List.of(b1, b2, b3)));

        // Act
        MonthlyTotal totals = new MonthlyTotalSum(calculator);

        // Assert
        assertEquals(MoneyAmount.ofCents(1500L), totals.monthlyTotal(BookingType.WITHDRAWAL));
        assertEquals(MoneyAmount.ofCents(200L), totals.monthlyTotal(BookingType.INTEREST));
        assertEquals(MoneyAmount.ZERO, totals.monthlyTotal(BookingType.TAX_PREVIOUS_YEAR));
    }

    @Test
    void monthlyTotals_emptyMonthReturnsZeros() {
        Bookings mockBookings = new Bookings() {
            @Override
            public OpeningBalance openingBalance() {
                return new OpeningBalance(MoneyAmount.ZERO);
            }

            @Override
            public MonthlyBookings bookingsInMonth(Month m) {
                return new MonthlyBookings(List.of());
            }
        };

        MonthlyTotalSum.MonthlySumTotalCalculator calculator = new MonthlyTotalSum.MonthlySumTotalCalculator(mockBookings, Month.JANUARY);
        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotals();

        for (BookingType type : BookingType.values()) {
            assertEquals(MoneyAmount.ZERO, totals.get(type), "Type " + type + " should be ZERO");
        }
    }

    @Test
    void monthlyTotals_handlesNegativeAmounts() {
        // Arrange
        Booking pos = createBooking(BookingType.WITHDRAWAL, 1000L, Month.MARCH, "Plus");
        Booking neg = createBooking(BookingType.WITHDRAWAL, -400L, Month.MARCH, "Minus");

        final MonthlyTotalSum.MonthlySumTotalCalculator calculator = createCalculator(new MonthlyBookings(List.of(pos, neg)));

        // Act
        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotals();

        // Assert
        assertEquals(MoneyAmount.ofCents(600L), totals.get(BookingType.WITHDRAWAL));
    }

    @Test
    void monthlyTotals_null() {
        final MonthlyTotalSum.MonthlySumTotalCalculator calculator = createCalculator(null);

        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotals();

        assertEquals(MoneyAmount.ZERO, totals.get(BookingType.WITHDRAWAL));
    }

    private static MonthlyTotalSum.MonthlySumTotalCalculator createCalculator(final MonthlyBookings monthlyBookings) {
        Bookings bookings = new Bookings() {
            @Override
            public OpeningBalance openingBalance() {
                return new OpeningBalance(MoneyAmount.ZERO);
            }

            @Override
            public MonthlyBookings bookingsInMonth(Month m) {
                return m == Month.MARCH ? monthlyBookings : new MonthlyBookings(List.of());
            }
        };

        return new MonthlyTotalSum.MonthlySumTotalCalculator(bookings, Month.MARCH);
    }

    private Booking createBooking(BookingType type, long cents, Month month, String comment) {
        return Booking.builder()
                .type(type)
                .amount(MoneyAmount.ofCents(cents))
                .date(LocalDate.of(2026, month, 1))
                .comment(comment)
                .build();
    }
}
