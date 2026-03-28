package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatement;
import de.nihas101.midas.accountstatement.dto.AccountStatements;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DefaultRunningTotalAccountStatements implements RunningTotalAccountStatements {

    // TODO: Don't expose this
    private final List<RunningTotalAccountStatement> runningTotalAccountStatements;

    public DefaultRunningTotalAccountStatements(
            final AccountStatements accountStatements,
            final List<BookingType> typeOrder
    ) {
        final OpeningBalance openingBalance = accountStatements.openingBalance();
        final List<AccountStatement> statements = typeOrder.stream()
                .map(accountStatements::forType)
                .toList();

        final List<RunningTotalAccountStatement> runningTotalAccountStatements = new ArrayList<>();

        MoneyAmount currentBalance = openingBalance.getOpeningBalance();
        // TODO: Add opening balance
        for (final AccountStatement statement : statements) {
            currentBalance = currentBalance.plus(statement.amount());
            runningTotalAccountStatements.add(
                    new DefaultRunningTotalAccountStatement(
                            statement,
                            currentBalance
                    )
            );
        }
        // TODO: Add closing balance

        this.runningTotalAccountStatements = runningTotalAccountStatements;
    }

    @Override
    public List<RunningTotalAccountStatement> runningTotalAccountStatements() {
        return runningTotalAccountStatements;
    }
}
