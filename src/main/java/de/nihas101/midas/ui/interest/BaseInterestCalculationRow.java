package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Locale;

@RequiredArgsConstructor
public class BaseInterestCalculationRow implements InterestCalculationRow {
    public static final int DEFAULT_INTEREST_DAYS_COUNT = 30;
    private final String monthAsString;
    private final MoneyAmount monthlyTotalSum;
    private final MoneyAmount balanceAtEndOfMonth;
    private final int interestDaysCount;
    private final BigDecimal interestAmount; // TODO: Use an appropriate class (MoneyAmount?)
    private final Locale locale;

    public BaseInterestCalculationRow(
            final String monthAsString,
            final MoneyAmount totalTransactionAmount,
            final MoneyAmount balanceAtEndOfMonth,
            final BigDecimal interestAmount, // TODO: Use an appropriate class (MoneyAmount?)
            final Locale locale
    ) {
        this(
                monthAsString,
                totalTransactionAmount,
                balanceAtEndOfMonth,
                DEFAULT_INTEREST_DAYS_COUNT,
                interestAmount,
                locale
        );
    }

    @Override
    public String monthAsString() {
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
    public int interestDaysCount() {
        return interestDaysCount;
    }

    @Override
    public BigDecimal interestAmount() {
        return interestAmount;
    }
}
