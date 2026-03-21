package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ZinsenRow implements InterestCalculationRow { // TODO: Rename into english
    private final MoneyAmount zinsen;

    @Override
    public String monthAsString() {
        return "Zinsen";
    } // TODO: i18n

    @Override
    public Transaction totalTransaction() {
        return new Transaction(MoneyAmount.ZERO);
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return new Transaction(zinsen);
    }

    @Override
    public int interestDaysCount() {
        return 0;
    }

    @Override
    public BigDecimal interestAmount() {
        return BigDecimal.ZERO;
    }
}
