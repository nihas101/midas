package de.nihas101.midas.bookings.row;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class BookingRowService {

    private final MessageSource messageSource;

    public List<BookingRow> generateRows(final Bookings bookings, final Locale locale) {
        List<BookingRow> rows = new ArrayList<>();
        rows.add(new OpeningBalanceBookingRow(bookings));
        rows.addAll(monthlySummaryRows(bookings, locale));
        return rows;
    }

    private List<BookingRow> monthlySummaryRows(final Bookings bookings, final Locale locale) {
        final List<BookingRow> rows = new ArrayList<>();
        final AtomicReference<MoneyAmount> currentBalance = new AtomicReference<>(
                bookings.openingBalance()
                        .getOpeningBalance()
        );

        final List<Month> months = Arrays.stream(Month.values())
                .filter(m -> !bookings.bookingsInMonth(m).bookings().isEmpty())
                .toList();

        if (months.isEmpty()) {
            return rows;
        }

        months.stream()
                .limit(months.size() - 1)
                .forEach(month -> {
                    final CumulativeSummaryBookingRow summaryBookingRow = generateBookingRows(
                            bookings,
                            month,
                            currentBalance.get(),
                            rows,
                            "single-separator",
                            locale
                    );
                    currentBalance.set(summaryBookingRow.balance());
                });

        generateBookingRows(bookings, months.getLast(), currentBalance.get(), rows, "double-separator", locale);

        return rows;
    }

    private CumulativeSummaryBookingRow generateBookingRows(
            final Bookings bookings,
            final Month month,
            final MoneyAmount curr,
            final List<BookingRow> rows,
            final String partName,
            final Locale locale
    ) {
        final CumulativeSummaryBookingRow cumulativeSummaryBookingRow = new CumulativeSummaryBookingRow(
                "",
                messageSource.getMessage("bookings.table.summary.cumulative", null, locale),
                bookings,
                month,
                partName
        );

        new SummarizingBookingsToBookingRowConverter(
                new DefaultBookingsToBookingRowConverter(
                        bookings,
                        month,
                        curr,
                        rows::add
                ),
                new MonthlySummaryBookingRow(
                        messageSource.getMessage("bookings.table.summary.monthly", null, locale),
                        bookings,
                        month
                ),
                cumulativeSummaryBookingRow,
                rows::add
        ).generate();

        return cumulativeSummaryBookingRow;
    }
}
