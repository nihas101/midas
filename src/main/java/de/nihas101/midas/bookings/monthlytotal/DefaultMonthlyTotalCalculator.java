package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultMonthlyTotalCalculator implements MonthlyTotalCalculator {

    private final Bookings bookings;
    private final Month month;

    // TODO: Cache
    // TODO: Wrap the return value
    @Override
    public Map<BookingType, MoneyAmount> monthlyTotal() {
        final MoneyAmount initialBalance = initialBalance();
        final MonthlyBookings monthBookings = bookings.bookingsInMonth(month);
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

    private MoneyAmount initialBalance() {
        MoneyAmount initialBalance = bookings.initialBalance();
        return initialBalance == null ? MoneyAmount.ZERO : initialBalance;
    }

    private Map<String, List<Booking>> groupByDate(final MonthlyBookings monthBookings) {
        if (monthBookings == null || monthBookings.bookings() == null) {
            return Collections.emptyMap();
        }

        return monthBookings.bookings()
                .stream()
                .collect(Collectors.groupingBy(b -> b.getDate().toString()));
    }
}
