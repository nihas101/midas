package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class ZinszahlSumRow implements InterestCalculationRow {
    private final BigDecimal sumOfZinsZahl; // TODO: Use english

    @Override
    public String monthAsString() {
        return "Summe Zinszahl";
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
        return sumOfZinsZahl.setScale(0, RoundingMode.HALF_UP);
    } // TODO: This does not seem to be calculated right yet, investigate
}
