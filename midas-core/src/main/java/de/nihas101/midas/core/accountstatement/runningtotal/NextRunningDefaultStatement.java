package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.accountstatement.NextRunningStatement;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.commons.MoneyAmount;

public class NextRunningDefaultStatement implements NextRunningStatement {

    private final RunningTotalAccountStatement nextRunningTotal;

    public NextRunningDefaultStatement(
            final LabeledAccountStatement statement,
            final MoneyAmount currentBalance
    ) {
        this.nextRunningTotal = new DefaultRunningTotalAccountStatement(
                statement,
                nextBalance(statement, currentBalance)
        );
    }

    private static MoneyAmount nextBalance(
            final LabeledAccountStatement statement,
            final MoneyAmount currentBalance
    ) {
        if (currentBalance == null) {
            return MoneyAmount.ZERO;
        } else if (!statement.isHidden()) {
            return currentBalance.plus(statement.amount());
        } else {
            return currentBalance;
        }
    }

    @Override
    public RunningTotalAccountStatement nextRunningTotal() {
        return nextRunningTotal;
    }
}