package de.nihas101.midas.core.export.interest;

import de.nihas101.midas.core.export.ExportDataSource;
import de.nihas101.midas.core.export.ExportTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InterestExportDataSource implements ExportDataSource {

    private final InterestRowExtractor interestRowExtractor;
    private final MessageSource messageSource;
    private final Locale locale;

    @Override
    public void export(final ExportTarget exportTarget) {
        final List<ExportRow> rows = interestRowExtractor.rows();
        if (rows.isEmpty()) {
            return;
        }

        exportTarget.export(
                getSheetName(messageSource, locale),
                getHeaders(messageSource, locale),
                rows.stream()
                        .map(this::toGenericRow)
                        .collect(Collectors.toList())
        );
    }

    private List<Object> toGenericRow(final ExportRow exportRow) {
        List<Object> row = new ArrayList<>();
        row.add(exportRow.shareholderId());
        row.add(exportRow.shareholderName());
        row.add(exportRow.date());
        row.add(exportRow.transactions());
        row.add(exportRow.transSH());
        row.add(exportRow.balance());
        row.add(exportRow.balanceSH());
        row.add(exportRow.days());
        row.add(exportRow.interestNumber());
        row.add(exportRow.rate());
        return row;
    }

    private String getSheetName(final MessageSource messageSource, final Locale locale) {
        return messageSource.getMessage("interest-calculation", null, locale);
    }

    private List<String> getHeaders(final MessageSource messageSource, final Locale locale) {
        return List.of(
                messageSource.getMessage("bookings.shareholder.display-id", null, locale),
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

}
