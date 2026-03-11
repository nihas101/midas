package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: Test
@RequiredArgsConstructor
public class BookingsToBookingRowConverter { // TODO: This class is very closely related to MonthlyTotalCalculator, think on if and how to best share code

    private final Bookings bookings;
    private final Month month;

    // TODO: Wrap this list
    public List<BookingRow> bookingRows() {
        final MoneyAmount initialBalance = bookings.initialBalance();
        final MonthlyBookings monthBookings = bookings.bookingsInMonth(month);

        // TODO: Why are we grouping by comment instead of by type here?
        // Group by date and comment within the month
        final Map<String, List<Booking>> groupedByEntry = monthBookings.bookings()
                .stream()
                .collect(Collectors.groupingBy(b -> b.getDate().toString() + "_" + (b.getComment() != null ? b.getComment() : "")));

        // Sort by date
        final List<String> sortedEntryKeys = groupedByEntry.keySet().stream().sorted().toList();

        final Map<BookingType, MoneyAmount> monthTotals = new EnumMap<>(BookingType.class);
        Arrays.stream(BookingType.values()).forEach(t -> monthTotals.put(t, MoneyAmount.ZERO));

        MoneyAmount runningTotal = initialBalance;
        final List<BookingRow> bookingRows = new ArrayList<>();
        for (String entryKey : sortedEntryKeys) {
            List<Booking> entryBookings = groupedByEntry.get(entryKey);
            Booking first = entryBookings.getFirst();

            final Map<BookingType, MoneyAmount> entryAmounts = new EnumMap<>(BookingType.class);
            MoneyAmount entryTotal = MoneyAmount.ZERO;

            for (final Booking b : entryBookings) {
                entryAmounts.put(b.getType(), b.getAmount());
                entryTotal = entryTotal.plus(b.getAmount());
                monthTotals.put(b.getType(), monthTotals.get(b.getType()).plus(b.getAmount()));
            }

            runningTotal = runningTotal.plus(entryTotal);
            final BookingRow bookingRow = new DefaultBookingRow( // TODO: Get rid of this dependency to the UI?
                    String.valueOf(first.getDisplayId()),
                    first.getDate().toString(),
                    first.getComment(),
                    entryAmounts,
                    entryTotal,
                    runningTotal
            );
            bookingRows.add(bookingRow);
        }
        return bookingRows;
    }
}
