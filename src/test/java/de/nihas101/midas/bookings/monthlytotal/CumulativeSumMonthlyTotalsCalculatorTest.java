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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;

import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CumulativeSumMonthlyTotalsCalculatorTest {

    @Test
    void nullTest() {
        Assertions.assertEquals(
                Arrays.stream(BookingType.values()).collect(
                        Collectors.toMap(
                                Function.identity(),
                                bookingType -> MoneyAmount.ZERO
                        )
                ),
                new CumulativeSumMonthlyTotalsCalculator(null, Month.JANUARY).monthlyTotals()
        );
    }

    @ParameterizedTest
    @MethodSource("monthlyTotalsArguments")
    void monthlyTotals(
            final Bookings bookings,
            final Month month,
            final Map<BookingType, MoneyAmount> expectedTotals
    ) {
        final CumulativeSumMonthlyTotalsCalculator calculator = new CumulativeSumMonthlyTotalsCalculator(bookings, month);
        final Map<BookingType, MoneyAmount> result = calculator.monthlyTotals();

        for (final BookingType type : BookingType.values()) {
            Assertions.assertEquals(
                    expectedTotals.getOrDefault(type, MoneyAmount.ZERO),
                    result.get(type),
                    "Mismatch for type " + type + " in month " + month
            );
        }
    }

    public static Stream<Arguments> monthlyTotalsArguments() {
        final Bookings bookings = mock(Bookings.class);

        // Setup test data
        // Jan: Withdrawal 100
        final Booking janW = Booking.builder().type(BookingType.WITHDRAWAL).amount(MoneyAmount.ofCents(100L)).build();
        when(bookings.bookingsInMonth(Month.JANUARY)).thenReturn(new FilteredBookings(List.of(janW)));

        // Feb: Interest 50
        final Booking febI = Booking.builder().type(BookingType.INTEREST).amount(MoneyAmount.ofCents(50L)).build();
        when(bookings.bookingsInMonth(Month.FEBRUARY)).thenReturn(new FilteredBookings(List.of(febI)));

        // Mar: Withdrawal 200, Tax Credit 300
        final Booking marW = Booking.builder().type(BookingType.WITHDRAWAL).amount(MoneyAmount.ofCents(200L)).build();
        final Booking marT = Booking.builder().type(BookingType.TAX_CREDIT).amount(MoneyAmount.ofCents(300L)).build();
        when(bookings.bookingsInMonth(Month.MARCH)).thenReturn(new FilteredBookings(List.of(marW, marT)));

        // Rest empty
        for (Month m : Month.values()) {
            if (m.getValue() > 3) {
                when(bookings.bookingsInMonth(m)).thenReturn(new FilteredBookings(Collections.emptyList()));
            }
        }

        return Stream.of(
                Arguments.of(
                        bookings,
                        Month.JANUARY,
                        Map.of(BookingType.WITHDRAWAL, MoneyAmount.ofCents(100L))
                ),
                Arguments.of(
                        bookings,
                        Month.FEBRUARY,
                        Map.of(
                                BookingType.WITHDRAWAL, MoneyAmount.ofCents(100L),
                                BookingType.INTEREST, MoneyAmount.ofCents(50L)
                        )
                ),
                Arguments.of(
                        bookings,
                        Month.MARCH,
                        Map.of(
                                BookingType.WITHDRAWAL, MoneyAmount.ofCents(300L),
                                BookingType.INTEREST, MoneyAmount.ofCents(50L),
                                BookingType.TAX_CREDIT, MoneyAmount.ofCents(300L)
                        )
                ),
                Arguments.of(
                        bookings,
                        Month.DECEMBER,
                        Map.of(
                                BookingType.WITHDRAWAL, MoneyAmount.ofCents(300L),
                                BookingType.INTEREST, MoneyAmount.ofCents(50L),
                                BookingType.TAX_CREDIT, MoneyAmount.ofCents(300L)
                        )
                )
        );
    }

    @ParameterizedTest
    @EnumSource(Month.class)
    void allTypesInitialized(Month month) {
        final Bookings bookings = mock(Bookings.class);
        when(bookings.bookingsInMonth(ArgumentMatchers.any(Month.class))).thenReturn(new FilteredBookings(Collections.emptyList()));

        final CumulativeSumMonthlyTotalsCalculator calculator = new CumulativeSumMonthlyTotalsCalculator(bookings, month);
        final Map<BookingType, MoneyAmount> result = calculator.monthlyTotals();

        Assertions.assertEquals(BookingType.values().length, result.size());
        for (BookingType type : BookingType.values()) {
            Assertions.assertTrue(result.containsKey(type));
            Assertions.assertEquals(MoneyAmount.ZERO, result.get(type));
        }
    }

}
