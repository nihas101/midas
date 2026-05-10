package de.nihas101.midas.interest.row;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.row.BookingRow;
import de.nihas101.midas.bookings.row.DefaultBookingsToBookingRowConverter;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class InterestRowService {

    private final MessageSource messageSource;

    public List<InterestCalculationRow> generateRows(
            final Year year,
            final Bookings bookings,
            final BigDecimal interestRate,
            final InterestCalculation interestCalculation,
            final Locale locale
    ) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        rows.add(
                new OpeningBalanceInterestCalculationRow(
                        bookings,
                        year,
                        interestRate,
                        messageSource,
                        locale
                )
        );

        rows.addAll(interestRows(year, bookings, interestCalculation, locale));

        rows.addAll(
                List.of(
                        new InterestSumRow(
                                interestCalculation.interestSum(),
                                messageSource,
                                locale
                        ),
                        new DivisorRow(
                                interestCalculation.divisor(),
                                messageSource,
                                locale
                        ),
                        new InterestRow(
                                interestCalculation.interest(),
                                messageSource,
                                locale
                        ),
                        new FinalSumRow(
                                year.atMonth(Month.DECEMBER).atEndOfMonth(),
                                interestCalculation.finalSum(),
                                messageSource,
                                locale
                        )
                )
        );
        return rows;
    }

    private List<InterestCalculationRow> interestRows(
            final Year year,
            final Bookings bookings,
            final InterestCalculation interestCalculation,
            final Locale locale
    ) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        final AtomicReference<MoneyAmount> currentBalance = new AtomicReference<>(
                bookings.openingBalance()
                        .getOpeningBalance()
        );

        final List<Month> months = Arrays.stream(Month.values())
                .filter(month -> !bookings.bookingsInMonth(month)
                        .bookings()
                        .isEmpty())
                .toList();

        if (months.isEmpty()) {
            return rows;
        }

        months.stream()
                .limit(months.size() - 1)
                .forEach(month -> generateInterestRow(
                                year,
                                bookings,
                                interestCalculation,
                                month,
                                currentBalance,
                                rows,
                                "no-separator-column",
                                locale
                        )
                );

        generateInterestRow(
                year,
                bookings,
                interestCalculation,
                months.getLast(),
                currentBalance,
                rows,
                "single-separator",
                locale
        );
        return rows;
    }

    private void generateInterestRow(
            final Year year,
            final Bookings bookings,
            final InterestCalculation interestCalculation,
            final Month month,
            final AtomicReference<MoneyAmount> currentBalance,
            final List<InterestCalculationRow> rows,
            final String partName,
            final Locale locale
    ) {
        final List<BookingRow> bookingRows = new ArrayList<>();
        new DefaultBookingsToBookingRowConverter(
                bookings,
                month,
                currentBalance.get(),
                bookingRows::add
        ).generate();

        if (!bookingRows.isEmpty()) {
            currentBalance.set(bookingRows.getLast().balance());
            rows.add(
                    new DefaultInterestCalculationRow(
                            year.atMonth(month),
                            interestCalculation.interests().get(month),
                            locale,
                            interestCalculation.monthlyBalances().get(month),
                            interestCalculation.monthlyTotalSums().get(month),
                            partName
                    )
            );
        }
    }
}
