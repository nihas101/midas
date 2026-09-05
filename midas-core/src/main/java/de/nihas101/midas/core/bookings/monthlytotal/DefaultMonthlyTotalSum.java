package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.api.bookings.MonthlyTotalSum;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultMonthlyTotalSum implements MonthlyTotalSum {

    private final Map<BookingType, MoneyAmount> monthlyTotals;
    public static final MonthlyTotalSum ZERO = new DefaultMonthlyTotalSum();

    private DefaultMonthlyTotalSum() {
        this(Collections.emptyMap());
    }

    public DefaultMonthlyTotalSum(
            final Bookings bookings,
            final Month month
    ) {
        this(new MonthlySumTotalCalculator(bookings, month));
    }

    public DefaultMonthlyTotalSum(final MonthlySumTotalCalculator monthlySumTotalCalculator) {
        this(monthlySumTotalCalculator.monthlyTotals());
    }

    @Override
    public MoneyAmount monthlyTotal(BookingType bookingType) {
        return monthlyTotals.getOrDefault(bookingType, MoneyAmount.ZERO);
    }

    @Override
    public MoneyAmount sum() {
        return monthlyTotals.values()
                .stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
    }

    @RequiredArgsConstructor
    public static class MonthlySumTotalCalculator {

        private final Bookings bookings;
        private final Month month;

        public Map<BookingType, MoneyAmount> monthlyTotals() {
            final OpeningBalance openingBalance = bookings.openingBalance();
            final MoneyAmount initialBalance = openingBalance != null
                    ? openingBalance.getOpeningBalance()
                    : MoneyAmount.ZERO;
            final FilteredBookings monthBookings = bookings.bookingsInMonth(month);
            final Map<String, List<Booking>> groupedBookings = groupByDate(monthBookings);
            final List<String> sortedDates = groupedBookings.keySet()
                    .stream()
                    .sorted()
                    .toList();

            return calculateMonthTotals(initialBalance, sortedDates, groupedBookings);
        }

        private Map<BookingType, MoneyAmount> calculateMonthTotals(final MoneyAmount initialBalance, final List<String> sortedDates, final Map<String, List<Booking>> groupedBookings) {
            final Map<BookingType, MoneyAmount> monthTotals = createBookingTypeToTotalMapping();

            MoneyAmount runningTotal = initialBalance;
            for (String date : sortedDates) {
                List<Booking> entryBookings = groupedBookings.get(date);

                MoneyAmount entryTotal = MoneyAmount.ZERO;

                for (final Booking booking : entryBookings) {
                    entryTotal = entryTotal.plus(booking.getAmount());
                    monthTotals.put(booking.getType(), monthTotals.get(booking.getType()).plus(booking.getAmount()));
                }

                runningTotal = runningTotal.plus(entryTotal);
            }
            return monthTotals;
        }

        private Map<BookingType, MoneyAmount> createBookingTypeToTotalMapping() {
            final Map<BookingType, MoneyAmount> monthTotals = new EnumMap<>(BookingType.class);
            Arrays.stream(BookingType.values()).forEach(t -> monthTotals.put(t, MoneyAmount.ZERO));
            return monthTotals;
        }

        private Map<String, List<Booking>> groupByDate(final FilteredBookings monthBookings) {
            if (monthBookings == null || monthBookings.bookings() == null) {
                return Collections.emptyMap();
            }

            return monthBookings.bookings()
                    .stream()
                    .collect(Collectors.groupingBy(b -> b.getDate().toString()));
        }
    }
}
