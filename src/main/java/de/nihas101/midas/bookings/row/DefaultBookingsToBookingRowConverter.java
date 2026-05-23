package de.nihas101.midas.bookings.row;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.FilteredBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// TODO: Test
@RequiredArgsConstructor
public class DefaultBookingsToBookingRowConverter implements BookingsToBookingRowConverter {

    private final FilteredBookings monthBookings;
    private final MoneyAmount startingBalance;
    private final DateTimeFormatter dateFormat;
    private final Consumer<BookingRow> consumer;

    public DefaultBookingsToBookingRowConverter(
            final Bookings bookings,
            final Month month,
            final MoneyAmount startingBalance,
            final Consumer<BookingRow> consumer
    ) {
        this(
                bookings.bookingsInMonth(month),
                startingBalance,
                DateTimeFormatter.ofPattern("dd.MM"),
                consumer
        );
    }

    @Override
    public void generate() {
        final Map<String, List<Booking>> groupedByEntry = monthBookings.bookings()
                .stream()
                .collect(Collectors.groupingBy(b -> b.getDate().toString() + "_" + (b.getComment() != null ? b.getComment() : "")));

        // Sort by date
        final List<String> sortedEntryKeys = groupedByEntry.keySet().stream().sorted().toList();

        final Map<BookingType, MoneyAmount> monthTotals = new EnumMap<>(BookingType.class);
        Arrays.stream(BookingType.values()).forEach(t -> monthTotals.put(t, MoneyAmount.ZERO));

        MoneyAmount runningTotal = startingBalance;
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
            final BookingRow bookingRow = new DefaultBookingRow(
                    String.valueOf(first.getDisplayId()),
                    first.getDate().format(dateFormat),
                    first.getComment(),
                    new MonthlyTotalSum(entryAmounts),
                    entryTotal,
                    MoneyAmount.ZERO,
                    entryBookings
            );
            consumer.accept(bookingRow);
        }
    }
}
