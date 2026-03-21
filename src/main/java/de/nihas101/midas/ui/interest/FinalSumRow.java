package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FinalSumRow implements InterestCalculationRow {
    private final LocalDate date;
    private final MoneyAmount sum;

    @Override
    public String monthAsString() {
        // TODO: Allow the user to define all these formats somewhere?
        return "Bestand per " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); // TODO: i18n
    }

    @Override
    public Transaction totalTransaction() {
        return null;
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return new Transaction(sum);
    }

    @Override
    public Integer interestDaysCount() {
        return null;
    }

    @Override
    public BigDecimal interestAmount() {
        return null;
    }
}
