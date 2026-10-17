package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.accountstatement.NextRunningStatement;
import de.nihas101.midas.api.accountstatement.NextRunningStatementFactory;
import de.nihas101.midas.commons.MoneyAmount;
import org.springframework.stereotype.Component;

@Component
public class NextRunningDefaultStatementFactory implements NextRunningStatementFactory {

    @Override
    public NextRunningStatement nextRunningStatement(
            final LabeledAccountStatement statement,
            final MoneyAmount currentBalance
    ) {
        return new NextRunningDefaultStatement(statement, currentBalance);
    }
}