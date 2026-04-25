package de.nihas101.midas.export.bookings;

import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class BookingsRowExtractor {

    public static final String TYPE_OPENING_BALANCE = "OPENING_BALANCE";

    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BookingsReader bookingsReader;
    private final OpeningBalanceService openingBalanceService;
    private final MessageSource messageSource;
    private final Locale locale;

    public List<ExportRow> rows() {
        return shareholders.stream()
                .flatMap(shareholder -> IntStream.rangeClosed(startDate.getYear(), endDate.getYear())
                        .mapToObj(yearValue -> exportRows(shareholder, yearValue))
                        .flatMap(Collection::stream))
                .sorted(Comparator.comparing(ExportRow::shareholderName).thenComparing(ExportRow::date))
                .toList();
    }

    private List<ExportRow> exportRows(final Shareholder shareholder, final int yearValue) {
        final List<ExportRow> exportRows = new ArrayList<>();
        final Year year = Year.of(yearValue);
        final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();
        final Optional<ExportRow> openingBalance = openingBalance(shareholder, year, shareholderName, yearValue);
        openingBalance.ifPresent(exportRows::add);
        final List<ExportRow> bookings = bookings(shareholder, year, shareholderName);
        exportRows.addAll(bookings);
        return exportRows;
    }

    private Optional<ExportRow> openingBalance(
            final Shareholder shareholder,
            final Year year,
            final String shareholderName,
            final int yearValue
    ) {
        final OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), year);
        if (openingBalance == null) {
            return Optional.empty();
        }
        final LocalDate openingBalanceDate = year.atDay(1);
        if (!isWithinRange(openingBalanceDate)) {
            return Optional.empty();
        }

        return Optional.of(
                new ExportRow(
                        shareholderName,
                        null,
                        openingBalanceDate,
                        messageSource.getMessage("export.bookings.opening-balance", new Object[]{yearValue}, locale),
                        TYPE_OPENING_BALANCE,
                        openingBalance.getOpeningBalance().toBigDecimal()
                )
        );
    }

    private List<ExportRow> bookings(
            final Shareholder shareholder,
            final Year year,
            final String shareholderName
    ) {
        return bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year)
                .filter(b -> isWithinRange(b.getDate()))
                .bookings()
                .stream()
                .map(b ->
                        new ExportRow(
                                shareholderName,
                                b.getId(),
                                b.getDate(),
                                b.getComment(),
                                b.getType().name(),
                                b.getAmount().toBigDecimal()
                        )
                ).toList();
    }

    private boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}