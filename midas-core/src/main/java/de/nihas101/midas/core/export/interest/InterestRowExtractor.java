package de.nihas101.midas.core.export.interest;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.sort.DisplayIdExportRowSort;
import de.nihas101.midas.core.export.sort.ExportRowSort;
import de.nihas101.midas.core.interest.InterestCalculation;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.interestamount.Interest;
import de.nihas101.midas.core.interest.row.TransactionType;
import de.nihas101.midas.core.interest.service.InterestRateService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
public class InterestRowExtractor {

    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BookingsReader bookingsReader;
    private final InterestRateService interestRateService;
    private final ExportRowSort exportRowSort;

    public InterestRowExtractor(
            final List<Shareholder> shareholders,
            final LocalDate startDate,
            final LocalDate endDate,
            final BookingsReader bookingsReader,
            final InterestRateService interestRateService
    ) {
        this(
                shareholders,
                startDate,
                endDate,
                bookingsReader,
                interestRateService,
                new DisplayIdExportRowSort()
        );
    }

    public List<ExportRow> rows() {
        return shareholders.stream()
                .flatMap(shareholder -> IntStream.rangeClosed(startDate.getYear(), endDate.getYear())
                        .mapToObj(Year::of)
                        .flatMap(year -> exportRows(shareholder, year)))
                .sorted(exportRowSort)
                .toList();
    }

    private Stream<ExportRow> exportRows(
            final Shareholder shareholder,
            final Year year
    ) {
        final BigDecimal rate = Optional.ofNullable(interestRateService.interestRate(shareholder.getId(), year))
                .map(InterestRate::getInterestRate)
                .orElse(ZERO);
        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation calc = new InterestCalculation(bookings, year, rate);
        return Arrays.stream(Month.values())
                .map(month -> {
                    final LocalDate date = year.atMonth(month).atEndOfMonth();
                    if (!isWithinRange(date)) {
                        return null;
                    }
                    final MoneyAmount trans = calc.monthlyTotalSums().get(month).sum();
                    final MoneyAmount bal = calc.monthlyBalances().get(month);
                    final Interest interest = calc.interests().get(month);

                    final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();
                    return new ExportRow(
                            shareholder.getDisplayId(),
                            shareholderName,
                            date,
                            trans.toBigDecimal().abs(),
                            trans.getCents() >= 0 ? TransactionType.CREDIT.getValue() : TransactionType.DEBIT.getValue(),
                            bal.toBigDecimal().abs(),
                            bal.getCents() >= 0 ? TransactionType.CREDIT.getValue() : TransactionType.DEBIT.getValue(),
                            interest.interestDays().intValue(),
                            interest.interestAmount().setScale(0, RoundingMode.HALF_UP),
                            rate
                    );
                })
                .filter(Objects::nonNull);
    }

    private boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}