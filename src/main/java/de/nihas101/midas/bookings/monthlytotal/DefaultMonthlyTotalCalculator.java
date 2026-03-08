package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Arrays;
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
        final MoneyAmount initialBalance = bookings.initialBalance();
        final List<Booking> monthBookings = bookings.groupBookingsByMonth(month);

        // TODO: Why are we grouping by comment instead of by type here or something?
        // Group by date and comment within the month
        final Map<String, List<Booking>> groupedByEntry = monthBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getDate().toString() + "_" + (b.getComment() != null ? b.getComment() : "")));

        // Sort by date
        final List<String> sortedEntryKeys = groupedByEntry.keySet().stream().sorted().toList();

        final Map<BookingType, MoneyAmount> monthTotals = new EnumMap<>(BookingType.class);
        Arrays.stream(BookingType.values()).forEach(t -> monthTotals.put(t, MoneyAmount.ZERO));

        MoneyAmount runningTotal = initialBalance;
        for (String entryKey : sortedEntryKeys) {
            List<Booking> entryBookings = groupedByEntry.get(entryKey);

            MoneyAmount entryTotal = MoneyAmount.ZERO;

            for (final Booking b : entryBookings) {
                entryTotal = entryTotal.plus(b.getAmount());
                monthTotals.put(b.getType(), monthTotals.get(b.getType()).plus(b.getAmount()));
            }

            runningTotal = runningTotal.plus(entryTotal);
        }
        return monthTotals;
    }
}
