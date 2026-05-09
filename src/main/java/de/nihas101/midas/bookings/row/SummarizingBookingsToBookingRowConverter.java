package de.nihas101.midas.bookings.row;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

// TODO: Test
@RequiredArgsConstructor
public class SummarizingBookingsToBookingRowConverter implements BookingsToBookingRowConverter {

    private final DefaultBookingsToBookingRowConverter delegate;
    private final MonthlySummaryBookingRow monthlySummaryBookingRow;
    private final CumulativeSummaryBookingRow cumulativeSummaryBookingRow;
    private final Consumer<BookingRow> consumer;

    @Override
    public void generate() {
        delegate.generate();
        consumer.accept(monthlySummaryBookingRow);
        consumer.accept(cumulativeSummaryBookingRow);
    }
}
