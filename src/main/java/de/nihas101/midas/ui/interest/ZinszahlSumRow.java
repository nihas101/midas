package de.nihas101.midas.ui.interest;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class ZinszahlSumRow implements InterestCalculationRow {
    private final BigDecimal sumOfZinsZahl; // TODO: Use english

    @Override
    public String monthAsString() {
        return "Summe Zinszahl"; // TODO: i18n
    }

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
        return sumOfZinsZahl.setScale(0, RoundingMode.HALF_UP);
    }
}
