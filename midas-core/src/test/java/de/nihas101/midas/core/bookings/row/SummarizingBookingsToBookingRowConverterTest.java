package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Collections;
import java.util.function.Consumer;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class SummarizingBookingsToBookingRowConverterTest {

    @Test
    void generate_delegatesAndEmitsMonthlyAndCumulativeRowsInOrder() {
        final DefaultBookingsToBookingRowConverter delegate = mock(DefaultBookingsToBookingRowConverter.class);
        final MonthlySummaryBookingRow monthlySummaryBookingRow = new MonthlySummaryBookingRow(
                "Monthly Summary",
                DefaultMonthlyTotalSum.ZERO,
                MoneyAmount.ZERO,
                MoneyAmount.ZERO,
                Collections.emptyList()
        );
        final CumulativeSummaryBookingRow cumulativeSummaryBookingRow = mock(CumulativeSummaryBookingRow.class);
        final Consumer<BookingRow> consumer = (Consumer<BookingRow>) mock(Consumer.class);

        final SummarizingBookingsToBookingRowConverter converter = new SummarizingBookingsToBookingRowConverter(
                delegate,
                monthlySummaryBookingRow,
                cumulativeSummaryBookingRow,
                consumer
        );

        converter.generate();

        final InOrder inOrder = inOrder(delegate, consumer);
        inOrder.verify(delegate).generate();
        inOrder.verify(consumer).accept(monthlySummaryBookingRow);
        inOrder.verify(consumer).accept(cumulativeSummaryBookingRow);
    }
}
