package de.nihas101.midas.ui.interest;

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
        return null;
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return null;
    }

    @Override
    public Integer interestDaysCount() {
        return null;
    }

    @Override
    public BigDecimal interestAmount() {
        return divisor.setScale(2, RoundingMode.HALF_UP);
    }
}
