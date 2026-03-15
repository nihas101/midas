package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
public class BaseInterestCalculationRow implements InterestCalculationRow {
    public static final int DEFAULT_INTEREST_DAYS_COUNT = 30;
    private final String monthAsString;
    private final MoneyAmount monthlyTotalSum;
    private final MoneyAmount balanceAtEndOfMonth;
    private final int interestDaysCount;
    private final int interestAmount;
    private final Locale locale;

    public BaseInterestCalculationRow(
            final String monthAsString,
            final MoneyAmount totalTransactionAmount,
            final MoneyAmount balanceAtEndOfMonth,
            final int interestAmount, // TODO: Wrap in class
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
    public String totalTransactionAmountAsString() {
        return toInterestCalculationAmountString(monthlyTotalSum);
    }

    @Override
    public String balanceAtEndOfMonth() {
        return toInterestCalculationAmountString(balanceAtEndOfMonth);
    }

    // TODO: This would be better in a print class that we inject
    private String toInterestCalculationAmountString(final MoneyAmount monthlyTotalSum) {
        if (monthlyTotalSum.smallerThan(MoneyAmount.ZERO)) {
            return monthlyTotalSum.abs().format(locale) + " H";
        } else {
            return monthlyTotalSum.format(locale) + " S";
        }
    }

    @Override
    public int interestDaysCount() {
        return interestDaysCount;
    }

    @Override
    public int interestAmount() {
        return interestAmount;
    }
}
