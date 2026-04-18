package de.nihas101.midas.export;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of ExportDataSource for the Bookings view.
 * It aggregates bookings and opening balances for multiple shareholders and formats them for export.
 */
@RequiredArgsConstructor
public class BookingsExportDataSource implements ExportDataSource {

    private static final String TYPE_OPENING_BALANCE = "OPENING_BALANCE";

    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BookingsReader bookingsReader;
    private final OpeningBalanceService openingBalanceService;
    private final MessageSource messageSource;
    private final Locale locale;

    @Override
    public String getSheetName(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("bookings", null, locale);
    }

    @Override
    public List<String> getHeaders(MessageSource messageSource, Locale locale) {
        return List.of(
                messageSource.getMessage("bookings.shareholder", null, locale),
                messageSource.getMessage("bookings.table.id", null, locale),
                messageSource.getMessage("bookings.date", null, locale),
                messageSource.getMessage("bookings.comment", null, locale),
                messageSource.getMessage("bookings.type.withdrawal", null, locale),
                messageSource.getMessage("bookings.type.tax-previous-year", null, locale),
                messageSource.getMessage("bookings.type.tax-credit", null, locale),
                messageSource.getMessage("bookings.type.interest", null, locale),
                messageSource.getMessage("bookings.type.compensation", null, locale),
                messageSource.getMessage("bookings.type.opening-balance", null, locale)
        );
    }

    @Override
    public List<List<Object>> getRows() {
        List<ExportRow> rawRows = new ArrayList<>();

        for (Shareholder shareholder : shareholders) {
            final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();

            for (int yearValue = startDate.getYear(); yearValue <= endDate.getYear(); yearValue++) {
                Year year = Year.of(yearValue);

                // 1. Handle Opening Balance
                final OpeningBalance ob = openingBalanceService.openingBalance(shareholder.getId(), year);
                if (ob != null) {
                    LocalDate obDate = year.atDay(1);
                    if (isWithinRange(obDate)) {
                        rawRows.add(new ExportRow(
                                shareholderName,
                                null,
                                obDate,
                                messageSource.getMessage("export.bookings.opening-balance", new Object[]{yearValue}, locale),
                                TYPE_OPENING_BALANCE,
                                ob.getOpeningBalance().toBigDecimal()
                        ));
                    }
                }

                // TODO: Handle this in a better way than exposing the booking in a list
                // 2. Handle Real Bookings
                bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year)
                        .allBookings().stream()
                        .filter(b -> isWithinRange(b.getDate()))
                        .forEach(b -> rawRows.add(
                                        new ExportRow(
                                                shareholderName,
                                                b.getId(),
                                                b.getDate(),
                                                b.getComment(),
                                                b.getType().name(),
                                                b.getAmount().toBigDecimal()
                                        )
                                )
                        );
            }
        }

        // Sort: Shareholder Name ASC, then Date ASC
        rawRows.sort(Comparator.comparing(ExportRow::shareholderName)
                .thenComparing(ExportRow::date));

        // Transform to grid format
        return rawRows.stream()
                .map(this::toObjectList)
                .collect(Collectors.toList());
    }

    private boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private List<Object> toObjectList(ExportRow row) {
        final List<Object> list = new ArrayList<>();
        list.add(row.shareholderName());
        list.add(row.id() != null ? row.id() : "");
        list.add(row.date());
        list.add(row.comment());

        // Map the amount to the correct column based on type, use 0.00 for others
        list.add(getValueForType(row, BookingType.WITHDRAWAL.name()));
        list.add(getValueForType(row, BookingType.TAX_PREVIOUS_YEAR.name()));
        list.add(getValueForType(row, BookingType.TAX_CREDIT.name()));
        list.add(getValueForType(row, BookingType.INTEREST.name()));
        list.add(getValueForType(row, BookingType.COMPENSATION.name()));
        list.add(getValueForType(row, TYPE_OPENING_BALANCE));

        return list;
    }

    private BigDecimal getValueForType(ExportRow row, String typeName) {
        if (Objects.equals(row.typeName(), typeName)) {
            return row.amount();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private record ExportRow(
            String shareholderName,
            Integer id,
            LocalDate date,
            String comment,
            String typeName,
            BigDecimal amount
    ) {
    }
}
