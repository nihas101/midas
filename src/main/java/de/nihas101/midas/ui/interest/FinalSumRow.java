package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FinalSumRow implements InterestCalculationRow {
    private final LocalDate date;
    private final MoneyAmount sum;

    @Override
    public String monthAsString() {
        // TODO: Allow the use to define all these formats somewhere?
        return "Bestand per " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); // TODO: i18n
    }

    @Override
    public Transaction totalTransaction() {
        return new Transaction(MoneyAmount.ZERO);
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return new Transaction(sum);
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
