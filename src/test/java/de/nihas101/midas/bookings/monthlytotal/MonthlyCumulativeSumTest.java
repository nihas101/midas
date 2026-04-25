package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.FilteredBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonthlyCumulativeSumTest {

    @Test
    void nullTest() {
        final MonthlyCumulativeSum monthlyCumulativeSum = new MonthlyCumulativeSum((MonthlyTotalsCalculator) null);
        Assertions.assertEquals(MoneyAmount.ZERO, monthlyCumulativeSum.sum());
        Assertions.assertEquals(MoneyAmount.ZERO, monthlyCumulativeSum.monthlyTotal(null));
    }

    @ParameterizedTest
    @MethodSource("cumulativeCalculationArguments")
    void cumulativeCalculation(
            final Bookings bookings,
            final Month month,
            final Map<BookingType, MoneyAmount> expectedTotals,
            final MoneyAmount expectedSum
    ) {
        final MonthlyCumulativeSum cumulativeSum = new MonthlyCumulativeSum(bookings, month);

        for (final BookingType type : BookingType.values()) {
            Assertions.assertEquals(
                    expectedTotals.getOrDefault(type, MoneyAmount.ZERO),
                    cumulativeSum.monthlyTotal(type),
                    "Mismatch for type: " + type + " in month " + month
            );
        }
        Assertions.assertEquals(expectedSum, cumulativeSum.sum(), "Total sum mismatch in month " + month);
    }

    public static Stream<Arguments> cumulativeCalculationArguments() {
        final Bookings bookings = mock(Bookings.class);

        // Jan: Withdrawal 100
        final Booking janW = Booking.builder().type(BookingType.WITHDRAWAL).amount(MoneyAmount.ofCents(100L)).build();
        when(bookings.bookingsInMonth(Month.JANUARY)).thenReturn(new FilteredBookings(List.of(janW)));

        // Feb: Interest 50
        final Booking febI = Booking.builder().type(BookingType.INTEREST).amount(MoneyAmount.ofCents(50L)).build();
        when(bookings.bookingsInMonth(Month.FEBRUARY)).thenReturn(new FilteredBookings(List.of(febI)));

        // Mar: Withdrawal 200, Compensation 300
        final Booking marW = Booking.builder().type(BookingType.WITHDRAWAL).amount(MoneyAmount.ofCents(200L)).build();
        final Booking marC = Booking.builder().type(BookingType.COMPENSATION).amount(MoneyAmount.ofCents(300L)).build();
        when(bookings.bookingsInMonth(Month.MARCH)).thenReturn(new FilteredBookings(List.of(marW, marC)));

        // Rest empty
        for (Month m : Month.values()) {
            if (m.getValue() > 3) {
                when(bookings.bookingsInMonth(m)).thenReturn(new FilteredBookings(List.of()));
            }
        }

        return Stream.of(
                Arguments.of(
                        bookings,
                        Month.JANUARY,
                        Map.of(BookingType.WITHDRAWAL, MoneyAmount.ofCents(100L)),
                        MoneyAmount.ofCents(100L)
                ),
                Arguments.of(
                        bookings,
                        Month.FEBRUARY,
                        Map.of(
                                BookingType.WITHDRAWAL, MoneyAmount.ofCents(100L),
                                BookingType.INTEREST, MoneyAmount.ofCents(50L)
                        ),
                        MoneyAmount.ofCents(150L)
                ),
                Arguments.of(
                        bookings,
                        Month.MARCH,
                        Map.of(
                                BookingType.WITHDRAWAL, MoneyAmount.ofCents(300L),
                                BookingType.INTEREST, MoneyAmount.ofCents(50L),
                                BookingType.COMPENSATION, MoneyAmount.ofCents(300L)
                        ),
                        MoneyAmount.ofCents(650L)
                ),
                Arguments.of(
                        bookings,
                        Month.APRIL,
                        Map.of(
                                BookingType.WITHDRAWAL, MoneyAmount.ofCents(300L),
                                BookingType.INTEREST, MoneyAmount.ofCents(50L),
                                BookingType.COMPENSATION, MoneyAmount.ofCents(300L)
                        ),
                        MoneyAmount.ofCents(650L)
                )
        );
    }
}
