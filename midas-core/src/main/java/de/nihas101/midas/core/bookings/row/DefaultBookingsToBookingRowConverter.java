package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.api.bookings.MonthlyTotalSum;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
            final DateTimeFormatter dateTimeFormatter,
            final Consumer<BookingRow> consumer
    ) {
        this(
                bookings.bookingsInMonth(month),
                startingBalance,
                dateTimeFormatter,
                consumer
        );
    }

    @Override
    public void generate() {
        final List<Booking> sortedBookings = monthBookings.bookings()
                .stream()
                .sorted(Comparator.comparing(Booking::getDate))
                .toList();

        MoneyAmount runningTotal = startingBalance;
        for (final Booking booking : sortedBookings) {
            runningTotal = runningTotal.plus(booking.getAmount());
            consumer.accept(
                    new DefaultBookingRow(
                            String.valueOf(booking.getDisplayId()),
                            booking.getDate().format(dateFormat),
                            booking.getComment(),
                            createMonthlyTotalSum(booking),
                            booking.getAmount(),
                            booking
                    )
            );
        }
    }

    private MonthlyTotalSum createMonthlyTotalSum(final Booking booking) {
        final Map<BookingType, MoneyAmount> entryAmounts = new EnumMap<>(BookingType.class);
        entryAmounts.put(booking.getType(), booking.getAmount());
        return new DefaultMonthlyTotalSum(entryAmounts);
    }

}
