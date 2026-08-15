package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.Transaction;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class BaseInterestCalculationRow implements InterestCalculationRow {
    public static final BigDecimal DEFAULT_INTEREST_DAYS = BigDecimal.valueOf(30L);

    private final String monthAsString;
    private final MoneyAmount monthlyTotalSum;
    private final MoneyAmount balanceAtEndOfMonth;
    private final BigDecimal interestDaysCount;
    private final BigDecimal interestAmount;

    public BaseInterestCalculationRow(
            final String monthAsString,
            final MoneyAmount totalTransactionAmount,
            final MoneyAmount balanceAtEndOfMonth,
            final BigDecimal interestAmount
    ) {
        this(
                monthAsString,
                totalTransactionAmount,
                balanceAtEndOfMonth,
                DEFAULT_INTEREST_DAYS,
                interestAmount
        );
    }

    @Override
    public String label() {
        return monthAsString;
    }

    @Override
    public Transaction totalTransaction() {
        return new Transaction(monthlyTotalSum); // TODO: Move into field
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return new Transaction(balanceAtEndOfMonth); // TODO: Move into field
    }

    @Override
    public BigDecimal interestDaysCount() {
        return interestDaysCount;
    }

    @Override
    public BigDecimal interestAmount() {
        return interestAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
