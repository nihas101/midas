package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.AccountStatementSort;
import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.accountstatement.NextRunningStatementFactory;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class OrderedRunningTotalAccountStatements implements RunningTotalAccountStatements {

    private final RunningTotalAccountStatements delegate;

    public OrderedRunningTotalAccountStatements(
            final Shareholder shareholder,
            final Year year,
            final AccountStatements accountStatements,
            final AccountStatementSort accountStatementSort,
            final NextRunningStatementFactory nextRunningStatementFactory,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                new DefaultRunningTotalAccountStatements(
                        accountStatementSort.sort(
                                statementPerBookingType(accountStatements),
                                shareholder,
                                year
                        ),
                        accountStatements != null ? accountStatements.openingBalance() : null,
                        new OpeningRunningTotalAccountStatement(
                                accountStatements != null ? accountStatements.openingBalance() : null,
                                messageSource,
                                locale,
                                year
                        ),
                        nextRunningStatementFactory
                )
        );
    }

    private static List<LabeledAccountStatement> statementPerBookingType(final AccountStatements accountStatements) {
        if (accountStatements == null) {
            return emptyList();
        }

        final List<LabeledAccountStatement> allStatements = new ArrayList<>();
        for (final BookingType type : BookingType.values()) {
            final LabeledAccountStatement stmt = accountStatements.forType(type);
            if (stmt != null) {
                allStatements.add(stmt);
            }
        }
        allStatements.addAll(accountStatements.manualStatements());
        return allStatements;
    }

    @Override
    public List<RunningTotalAccountStatement> runningTotalAccountStatements() {
        return delegate.runningTotalAccountStatements();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
}
