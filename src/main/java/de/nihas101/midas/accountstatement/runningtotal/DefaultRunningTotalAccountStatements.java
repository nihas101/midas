package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatements;
import de.nihas101.midas.accountstatement.dto.LabeledAccountStatement;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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
