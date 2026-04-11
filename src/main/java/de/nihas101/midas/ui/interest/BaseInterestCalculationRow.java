package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class BaseInterestCalculationRow implements InterestCalculationRow { // TODO: Get rid of this class and merge it with the ones using it as delegate
    public static final int DEFAULT_INTEREST_DAYS_COUNT = 30;
    private final String monthAsString;
    private final MoneyAmount monthlyTotalSum;
    private final MoneyAmount balanceAtEndOfMonth;
    private final int interestDaysCount;
    private final BigDecimal interestAmount; // TODO: A class for this?

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
