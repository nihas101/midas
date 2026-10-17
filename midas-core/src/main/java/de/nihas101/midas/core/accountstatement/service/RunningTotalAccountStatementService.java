package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.api.accountstatement.AccountStatementSort;
import de.nihas101.midas.api.accountstatement.NextRunningStatementFactory;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.accountstatement.runningtotal.OrderedRunningTotalAccountStatements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunningTotalAccountStatementService {

    private final DefaultAccountStatementService accountStatementService;
    private final AccountStatementSort accountStatementSort;
    private final NextRunningStatementFactory nextRunningStatementFactory;

    public RunningTotalAccountStatements runningTotalAccountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        return new OrderedRunningTotalAccountStatements(
                shareholder,
                year,
                accountStatementService.accountStatements(
                        shareholder,
                        year,
                        messageSource,
                        locale
                ),
                accountStatementSort,
                nextRunningStatementFactory,
                messageSource,
                locale
        );
    }

}
