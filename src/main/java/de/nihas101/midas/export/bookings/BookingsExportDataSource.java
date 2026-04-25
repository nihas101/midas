package de.nihas101.midas.export.bookings;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.export.ExportDataSource;
import de.nihas101.midas.export.ExportTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
public class BookingsExportDataSource implements ExportDataSource {

    private final BookingsRowExtractor bookingsRowExtractor;
    private final MessageSource messageSource;
    private final Locale locale;

    private String getSheetName(final MessageSource messageSource, final Locale locale) {
        return messageSource.getMessage("bookings", null, locale);
    }

    private List<String> getHeaders(final MessageSource messageSource, final Locale locale) {
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
    public void export(final ExportTarget exportTarget) {
        final List<ExportRow> rows = bookingsRowExtractor.rows();
        if (rows.isEmpty()) {
            return;
        }

        exportTarget.export(
                getSheetName(messageSource, locale),
                getHeaders(messageSource, locale),
                rows.stream()
                        .map(this::toGenericRow)
                        .toList()
        );
    }

    private List<Object> toGenericRow(ExportRow row) {
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
        list.add(getValueForType(row, BookingsRowExtractor.TYPE_OPENING_BALANCE));

        return list;
    }

    private BigDecimal getValueForType(ExportRow row, String typeName) {
        if (Objects.equals(row.typeName(), typeName)) {
            return row.amount();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

}
