package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: Test
@RequiredArgsConstructor
public class BookingsToBookingRowConverter {

    private final Bookings bookings;
    private final Month month;
    private final MoneyAmount startingBalance;
    private final DateTimeFormatter dateFormat;

    public BookingsToBookingRowConverter(Bookings bookings, Month month, MoneyAmount startingBalance) {
        this(
                bookings,
                month,
                startingBalance,
                DateTimeFormatter.ofPattern("dd.MM")
        );
    }

    // TODO: Wrap this list and merge the logic here into that class
    public List<BookingRow> bookingRows() {
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

        MoneyAmount runningTotal = startingBalance;
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
                    first.getDate().format(dateFormat),
                    first.getComment(),
                    new MonthlyTotalSum(entryAmounts),
                    entryTotal,
                    MoneyAmount.ZERO,
                    entryBookings
            );
            bookingRows.add(bookingRow);
        }
        return bookingRows;
    }
}
