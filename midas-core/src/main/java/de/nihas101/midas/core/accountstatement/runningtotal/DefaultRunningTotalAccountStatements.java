package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
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

    // TODO: Don't expose this
    private final List<RunningTotalAccountStatement> runningTotalAccountStatements;

    public DefaultRunningTotalAccountStatements(
            final AccountStatements accountStatements,
            final List<BookingType> typeOrder,
            final OpeningRunningTotalAccountStatement openingRunningTotalAccountStatement
    ) {
        final OpeningBalance openingBalance = accountStatements.openingBalance();
        this.runningTotalAccountStatements = new ArrayList<>();
        if (openingBalance == null) {
            return;
        }

        runningTotalAccountStatements.add(openingRunningTotalAccountStatement);
        final List<LabeledAccountStatement> statements = typeOrder.stream()
                .map(accountStatements::forType)
                .toList();

        MoneyAmount currentBalance = openingBalance.getOpeningBalance();
        for (final LabeledAccountStatement statement : statements) {
            currentBalance = currentBalance.plus(statement.amount());
            runningTotalAccountStatements.add(
                    new DefaultRunningTotalAccountStatement(
                            statement,
                            currentBalance
                    )
            );
        }

        for (final LabeledAccountStatement statement : accountStatements.manualStatements()) {
            currentBalance = currentBalance.plus(statement.amount());
            runningTotalAccountStatements.add(
                    new DefaultRunningTotalAccountStatement(
                            statement,
                            currentBalance
                    )
            );
        }
    }

    public DefaultRunningTotalAccountStatements(
            final List<LabeledAccountStatement> orderedStatements,
            final OpeningBalance openingBalance,
            final OpeningRunningTotalAccountStatement openingRunningTotalAccountStatement
    ) {
        log.info("Opening Balance: {}", openingBalance);
        this.runningTotalAccountStatements = new ArrayList<>();

        runningTotalAccountStatements.add(openingRunningTotalAccountStatement);

        MoneyAmount currentBalance = ensureOpeningBalanceAmount(openingBalance);
        for (final LabeledAccountStatement statement : orderedStatements) {
            currentBalance = currentBalance.plus(statement.amount());
            runningTotalAccountStatements.add(
                    new DefaultRunningTotalAccountStatement(
                            statement,
                            currentBalance
                    )
            );
        }
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
