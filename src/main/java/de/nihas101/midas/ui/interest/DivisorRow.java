package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class DivisorRow implements InterestCalculationRow {
    private final BigDecimal divisor;

    @Override
    public String monthAsString() {
        return "Divisor";
    } // TODO: i18n

    @Override
    public Transaction totalTransaction() {
        return new Transaction(MoneyAmount.ZERO);
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return new Transaction(MoneyAmount.ZERO);
    }

    @Override
    public int interestDaysCount() {
        return 0;
    }

    @Override
    public BigDecimal interestAmount() {
        return divisor.setScale(2, RoundingMode.HALF_UP);
    }
}
