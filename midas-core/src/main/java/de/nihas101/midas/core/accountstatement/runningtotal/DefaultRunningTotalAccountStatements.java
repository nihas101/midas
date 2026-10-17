package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.accountstatement.NextRunningStatementFactory;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultRunningTotalAccountStatements implements RunningTotalAccountStatements {

    private final List<RunningTotalAccountStatement> runningTotalAccountStatements;

    public DefaultRunningTotalAccountStatements(
            final List<LabeledAccountStatement> statements,
            final OpeningBalance openingBalance,
            final OpeningRunningTotalAccountStatement openingRunningTotalAccountStatement,
            final NextRunningStatementFactory nextRunningStatementFactory
    ) {
        final ArrayList<RunningTotalAccountStatement> runningTotalStatements = new ArrayList<>();
        runningTotalStatements.add(openingRunningTotalAccountStatement);

        MoneyAmount currentBalance = ensureOpeningBalanceAmount(openingBalance);
        for (final LabeledAccountStatement statement : statements) {
            final RunningTotalAccountStatement nextRunningTotal = nextRunningStatementFactory.nextRunningStatement(
                    statement,
                    currentBalance
            ).nextRunningTotal();
            currentBalance = nextRunningTotal.currentBalance();
            runningTotalStatements.add(nextRunningTotal);
        }
        this.runningTotalAccountStatements = runningTotalStatements;
    }

    private MoneyAmount ensureOpeningBalanceAmount(final OpeningBalance openingBalance) {
        return openingBalance != null ? openingBalance.getOpeningBalance() : MoneyAmount.ZERO;
    }

    @Override
    public List<RunningTotalAccountStatement> runningTotalAccountStatements() {
        return runningTotalAccountStatements;
    }

    @Override
    public boolean isEmpty() {
        return runningTotalAccountStatements == null || runningTotalAccountStatements.isEmpty();
    }
}
