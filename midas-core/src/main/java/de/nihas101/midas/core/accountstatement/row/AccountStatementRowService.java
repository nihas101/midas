package de.nihas101.midas.core.accountstatement.row;

import de.nihas101.midas.core.accountstatement.runningtotal.RunningTotalAccountStatements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountStatementRowService {

    private final MessageSource messageSource;

    public List<AccountStatementRow> generateRows(
            final RunningTotalAccountStatements accountStatements,
            final boolean withHidden
    ) {
        Stream<RunningTotalAccountStatementRow> runningTotalAccountStatementRowStream = accountStatements.runningTotalAccountStatements()
                .stream()
                .map(RunningTotalAccountStatementRow::new);

        if (!withHidden) {
            runningTotalAccountStatementRowStream = runningTotalAccountStatementRowStream
                    .filter(r -> !r.isHidden());
        }

        return runningTotalAccountStatementRowStream.collect(Collectors.toList());
    }

    public AccountStatementRow generateClosingRow(final RunningTotalAccountStatements accountStatements, final Locale locale) {
        return new ClosingAccountStatementRow(
                accountStatements,
                messageSource,
                locale
        );
    }
}
