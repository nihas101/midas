package de.nihas101.midas.export.interest;

import de.nihas101.midas.export.ExportDataSource;
import de.nihas101.midas.export.ExportTarget;
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
        List<Object> list = new ArrayList<>();
        list.add(exportRow.shareholderName());
        list.add(exportRow.date());
        list.add(exportRow.transactions());
        list.add(exportRow.transSH());
        list.add(exportRow.balance());
        list.add(exportRow.balanceSH());
        list.add(exportRow.days());
        list.add(exportRow.interestNumber());
        list.add(exportRow.rate());
        return list;
    }

    private String getSheetName(final MessageSource messageSource, final Locale locale) {
        return messageSource.getMessage("interest-calculation", null, locale);
    }

    private List<String> getHeaders(final MessageSource messageSource, final Locale locale) {
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

}
