package de.nihas101.midas.core.accountstatement.row;

import de.nihas101.midas.api.accountstatement.AccountStatementRow;
import de.nihas101.midas.api.accountstatement.AccountStatementRowService;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.core.config.AccountStatementConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAccountStatementRowService implements AccountStatementRowService {

    private final MessageSource messageSource;
    private final AccountStatementConfig accountStatementConfig;

    @Override
    public List<AccountStatementRow> generateRows(
            final RunningTotalAccountStatements accountStatements,
            final boolean withHidden
    ) {
        final String dateFormat = dateFormat();

        Stream<RunningTotalAccountStatementRow> runningTotalAccountStatementRowStream = accountStatements.runningTotalAccountStatements()
                .stream()
                .map(statement -> new RunningTotalAccountStatementRow(statement, dateFormat));

        if (!withHidden) {
            runningTotalAccountStatementRowStream = runningTotalAccountStatementRowStream
                    .filter(r -> !r.isHidden());
        }

        return runningTotalAccountStatementRowStream.collect(Collectors.toList());
    }

    private String dateFormat() {
        return accountStatementConfig != null && StringUtils.isNotBlank(accountStatementConfig.getDateFormat())
                ? accountStatementConfig.getDateFormat()
                : AccountStatementConfig.DEFAULT_DATE_FORMAT;
    }

    @Override
    public AccountStatementRow generateClosingRow(
            final RunningTotalAccountStatements accountStatements,
            final Locale locale
    ) {
        return new ClosingAccountStatementRow(
                accountStatements,
                messageSource,
                locale
        );
    }
}
