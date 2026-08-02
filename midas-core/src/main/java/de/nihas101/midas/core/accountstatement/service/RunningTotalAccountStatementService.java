package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.core.accountstatement.dto.AccountStatements;
import de.nihas101.midas.core.accountstatement.dto.LabeledAccountStatement;
import de.nihas101.midas.core.accountstatement.runningtotal.DefaultRunningTotalAccountStatements;
import de.nihas101.midas.core.accountstatement.runningtotal.OpeningRunningTotalAccountStatement;
import de.nihas101.midas.core.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.core.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunningTotalAccountStatementService {

    private final DefaultAccountStatementService accountStatementService;
    private final AccountStatementSort accountStatementSort;

    public RunningTotalAccountStatements runningTotalAccountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final AccountStatements accountStatements = accountStatementService.accountStatements(
                shareholder,
                year,
                messageSource,
                locale
        );
        final OpeningBalance openingBalance = accountStatements.openingBalance();

        final List<LabeledAccountStatement> allStatements = accountStatementSort.sort(
                statementPerBookingType(accountStatements),
                shareholder,
                year
        );

        return new DefaultRunningTotalAccountStatements(
                allStatements,
                openingBalance,
                new OpeningRunningTotalAccountStatement(
                        openingBalance,
                        messageSource,
                        locale
                )
        );
    }

    private List<LabeledAccountStatement> statementPerBookingType(final AccountStatements accountStatements) {
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

}
