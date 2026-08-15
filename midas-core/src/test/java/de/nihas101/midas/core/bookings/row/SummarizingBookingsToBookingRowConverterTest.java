package de.nihas101.midas.core.bookings.row;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SummarizingBookingsToBookingRowConverterTest {

    @Mock
    private DefaultBookingsToBookingRowConverter delegate;

    @Mock
    private MonthlySummaryBookingRow monthlySummaryBookingRow;

    @Mock
    private CumulativeSummaryBookingRow cumulativeSummaryBookingRow;

    @Mock
    private Consumer<BookingRow> consumer;

    @InjectMocks
    private SummarizingBookingsToBookingRowConverter converter;

    @Test
    void generateCallsDelegateAndConsumesSummaryRows() {
        converter.generate();

        verify(delegate).generate();
        verify(consumer).accept(monthlySummaryBookingRow);
        verify(consumer).accept(cumulativeSummaryBookingRow);
    }
}
