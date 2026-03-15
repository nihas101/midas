package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonthlySumTotalCalculatorTest {

    @ParameterizedTest
    @EnumSource(Month.class)
    void monthlyTotal_sumsAmountsByType(Month testMonth) {
        // Arrange
        Booking b1 = createBooking(BookingType.WITHDRAWAL, 1000L, testMonth, "A");
        Booking b2 = createBooking(BookingType.WITHDRAWAL, 500L, testMonth, "B");
        Booking b3 = createBooking(BookingType.INTEREST, 200L, testMonth, "C");

        MonthlySumTotalCalculator calculator = createCalculator(new MonthlyBookings(List.of(b1, b2, b3)));

        // Act
        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotal();

        // Assert
        assertEquals(MoneyAmount.ofCents(1500L), totals.get(BookingType.WITHDRAWAL));
        assertEquals(MoneyAmount.ofCents(200L), totals.get(BookingType.INTEREST));
        assertEquals(MoneyAmount.ZERO, totals.get(BookingType.TAX_PREVIOUS_YEAR));
    }

    @Test
    void monthlyTotal_emptyMonthReturnsZeros() {
        Bookings mockBookings = new Bookings() {
            @Override
            public MoneyAmount openingBalance() {
                return MoneyAmount.ZERO;
            }

            @Override
            public MonthlyBookings bookingsInMonth(Month m) {
                return new MonthlyBookings(List.of());
            }
        };

        MonthlySumTotalCalculator calculator = new MonthlySumTotalCalculator(mockBookings, Month.JANUARY);
        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotal();

        for (BookingType type : BookingType.values()) {
            assertEquals(MoneyAmount.ZERO, totals.get(type), "Type " + type + " should be ZERO");
        }
    }

    @Test
    void monthlyTotal_handlesNegativeAmounts() {
        // Arrange
        Booking pos = createBooking(BookingType.WITHDRAWAL, 1000L, Month.MARCH, "Plus");
        Booking neg = createBooking(BookingType.WITHDRAWAL, -400L, Month.MARCH, "Minus");

        final MonthlySumTotalCalculator calculator = createCalculator(new MonthlyBookings(List.of(pos, neg)));

        // Act
        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotal();

        // Assert
        assertEquals(MoneyAmount.ofCents(600L), totals.get(BookingType.WITHDRAWAL));
    }

    @Test
    void monthlyTotal_null() {
        final MonthlySumTotalCalculator calculator = createCalculator(null);

        Map<BookingType, MoneyAmount> totals = calculator.monthlyTotal();

        assertEquals(MoneyAmount.ZERO, totals.get(BookingType.WITHDRAWAL));
    }

    private static MonthlySumTotalCalculator createCalculator(final MonthlyBookings monthlyBookings) {
        Bookings bookings = new Bookings() {
            @Override
            public MoneyAmount openingBalance() {
                return MoneyAmount.ZERO;
            }

            @Override
            public MonthlyBookings bookingsInMonth(Month m) {
                return m == Month.MARCH ? monthlyBookings : new MonthlyBookings(List.of());
            }
        };

        return new MonthlySumTotalCalculator(bookings, Month.MARCH);
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
