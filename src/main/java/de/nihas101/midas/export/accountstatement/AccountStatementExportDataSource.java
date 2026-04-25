package de.nihas101.midas.export.accountstatement;

import de.nihas101.midas.export.ExportDataSource;
import de.nihas101.midas.export.ExportTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class AccountStatementExportDataSource implements ExportDataSource {

    private final AccountStatementsRowExtractor accountStatementsRowExtractor;
    private final MessageSource messageSource;
    private final Locale locale;

    private String getSheetName(final MessageSource messageSource, final Locale locale) {
        return messageSource.getMessage("account-statements", null, locale);
    }

    private List<String> getHeaders(final MessageSource messageSource, final Locale locale) {
        return List.of(
                messageSource.getMessage("bookings.shareholder", null, locale),
                messageSource.getMessage("account-statements.table.id", null, locale),
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );
    }

    @Override
    public void export(final ExportTarget exportTarget) {
        final List<ExportRow> rows = accountStatementsRowExtractor.rows();
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

    private List<Object> toGenericRow(final ExportRow exportRow) {
        List<Object> row = new ArrayList<>();
        row.add(exportRow.shareholderName());
        row.add(exportRow.id() != null ? exportRow.id() : "");
        row.add(exportRow.date());
        row.add(exportRow.type());
        row.add(exportRow.debit());
        row.add(exportRow.credit());
        row.add(exportRow.balance());
        return row;
    }
}
