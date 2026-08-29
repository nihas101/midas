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
    private final BigDecimal interestDaysCount;
    private final BigDecimal interestAmount;
    private final Transaction totalTransaction;
    private final Transaction balanceAtEndOfMonth;

    public BaseInterestCalculationRow(
            final String monthAsString,
            final MoneyAmount totalTransactionAmount,
            final MoneyAmount balanceAtEndOfMonth,
            final BigDecimal interestAmount
    ) {
        this(
                monthAsString,
                DEFAULT_INTEREST_DAYS,
                interestAmount,
                new Transaction(totalTransactionAmount),
                new Transaction(balanceAtEndOfMonth)
        );
    }

    @Override
    public String label() {
        return monthAsString;
    }

    @Override
    public Transaction totalTransaction() {
        return totalTransaction;
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return balanceAtEndOfMonth;
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
