package de.nihas101.midas.export;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.interestamount.Interest;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.ui.interest.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ExportDataSource for the Interest Calculation view.
 * It aggregates monthly interest results for multiple shareholders and years.
 */
@RequiredArgsConstructor
public class InterestExportDataSource implements ExportDataSource {

    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BookingsReader bookingsReader;
    private final InterestRateService interestRateService;
    private final MessageSource messageSource;
    private final Locale locale;

    @Override
    public void export(final ExportTarget exportTarget) {
        exportTarget.export(
                getSheetName(messageSource, locale),
                getHeaders(messageSource, locale),
                getRows()
        );
    }

    private String getSheetName(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("interest-calculation", null, locale);
    }

    private List<String> getHeaders(MessageSource messageSource, Locale locale) {
        return List.of(
                messageSource.getMessage("bookings.shareholder", null, locale),
                messageSource.getMessage("interest.table.month", null, locale),
                messageSource.getMessage("interest.table.transactions", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.balance", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.days", null, locale),
                messageSource.getMessage("interest.table.interest-amount", null, locale),
                messageSource.getMessage("interest.rate.label", null, locale)
        );
    }

    private List<List<Object>> getRows() {
        List<ExportRow> rawRows = new ArrayList<>();

        for (Shareholder shareholder : shareholders) {
            final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();

            for (int yearValue = startDate.getYear(); yearValue <= endDate.getYear(); yearValue++) {
                Year year = Year.of(yearValue);
                BigDecimal rate = Optional.ofNullable(interestRateService.interestRate(shareholder.getId(), year))
                        .map(InterestRate::getInterestRate)
                        .orElse(BigDecimal.ZERO);

                Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year);
                InterestCalculation calc = new InterestCalculation(bookings, year, rate);

                for (Month month : Month.values()) {
                    LocalDate date = year.atMonth(month).atEndOfMonth();
                    if (isWithinRange(date)) {
                        MoneyAmount trans = calc.monthlyTotalSums().get(month).sum();
                        MoneyAmount bal = calc.monthlyBalances().get(month);
                        Interest interest = calc.interests().get(month);

                        rawRows.add(new ExportRow(
                                shareholderName,
                                date,
                                trans.toBigDecimal().abs(),
                                trans.getCents() >= 0 ? TransactionType.CREDIT.getValue() : TransactionType.DEBIT.getValue(),
                                bal.toBigDecimal().abs(),
                                bal.getCents() >= 0 ? TransactionType.CREDIT.getValue() : TransactionType.DEBIT.getValue(),
                                interest.interestDays().intValue(),
                                interest.interestAmount().setScale(0, RoundingMode.HALF_UP),
                                rate
                        ));
                    }
                }
            }
        }

        // Sort: Shareholder Name ASC, then Date ASC
        rawRows.sort(Comparator.comparing(ExportRow::shareholderName)
                .thenComparing(ExportRow::date));

        return rawRows.stream()
                .map(r -> {
                    List<Object> list = new ArrayList<>();
                    list.add(r.shareholderName());
                    list.add(r.date());
                    list.add(r.transactions());
                    list.add(r.transSH());
                    list.add(r.balance());
                    list.add(r.balanceSH());
                    list.add(r.days());
                    list.add(r.interestNumber());
                    list.add(r.rate());
                    return list;
                })
                .collect(Collectors.toList());
    }

    private boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private record ExportRow(
            String shareholderName,
            LocalDate date,
            BigDecimal transactions,
            String transSH,
            BigDecimal balance,
            String balanceSH,
            Integer days,
            BigDecimal interestNumber,
            BigDecimal rate
    ) {}
}
