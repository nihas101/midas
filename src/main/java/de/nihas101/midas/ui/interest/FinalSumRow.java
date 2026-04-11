package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RequiredArgsConstructor
public class FinalSumRow implements InterestCalculationRow {
    private final MoneyAmount sum;
    private final String label;

    public FinalSumRow(
            final LocalDate date,
            final MoneyAmount sum,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                sum,
                messageSource.getMessage(
                        "interest.summary.final-sum",
                        // TODO: Allow the user to define all these formats somewhere?
                        new Object[]{date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))},
                        locale
                )
        );
    }

    @Override
    public String label() {
        return label;
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

    @Override
    public String partName() {
        return "double-separator";
    }
}
