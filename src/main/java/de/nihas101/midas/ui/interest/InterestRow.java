package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class InterestRow implements InterestCalculationRow {
    private final MoneyAmount interest;

    @Override
    public String monthAsString() {
        return "Zinsen"; // TODO: i18n
    }

    @Override
    public Transaction totalTransaction() {
        return null;
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return new Transaction(interest);
    }

    @Override
    public Integer interestDaysCount() {
        return null;
    }

    @Override
    public BigDecimal interestAmount() {
        return null;
    }

    @Override
    public String partName() {
        return "single-separator";
    }
}
