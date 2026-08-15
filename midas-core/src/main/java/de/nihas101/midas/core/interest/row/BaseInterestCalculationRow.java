package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.Transaction;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class BaseInterestCalculationRow implements InterestCalculationRow {
    public static final int DEFAULT_INTEREST_DAYS_COUNT = 30;
    private final String monthAsString;
    private final MoneyAmount monthlyTotalSum;
    private final MoneyAmount balanceAtEndOfMonth;
    private final int interestDaysCount;
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
                DEFAULT_INTEREST_DAYS_COUNT,
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
    public Integer interestDaysCount() {
        return interestDaysCount;
    }

    @Override
    public BigDecimal interestAmount() {
        return interestAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
