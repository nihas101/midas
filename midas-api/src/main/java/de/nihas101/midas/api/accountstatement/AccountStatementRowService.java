package de.nihas101.midas.api.accountstatement;

import java.util.List;
import java.util.Locale;

public interface AccountStatementRowService {
    List<AccountStatementRow> generateRows(
            final RunningTotalAccountStatements accountStatements,
            final boolean withHidden
    );

    AccountStatementRow generateClosingRow(final RunningTotalAccountStatements accountStatements, final Locale locale);
}
