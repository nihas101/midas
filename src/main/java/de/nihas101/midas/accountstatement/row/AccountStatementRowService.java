package de.nihas101.midas.accountstatement.row;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountStatementRowService {

    private final MessageSource messageSource;

    public List<AccountStatementRow> generateRows(final RunningTotalAccountStatements accountStatements) {
        return accountStatements.runningTotalAccountStatements()
                .stream()
                .map(RunningTotalAccountStatementRow::new)
                .collect(Collectors.toList());
    }

    public AccountStatementRow generateClosingRow(final RunningTotalAccountStatements accountStatements, final Locale locale) {
        return new ClosingAccountStatementRow(
                accountStatements,
                messageSource,
                locale
        );
    }
}
