package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.Transaction;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.config.DatesConfig;
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
            final DatesConfig datesConfig,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                sum,
                messageSource.getMessage(
                        "interest.summary.final-sum",
                        new Object[]{date.format(DateTimeFormatter.ofPattern(datesConfig.getLongDateFormat()))},
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
    public BigDecimal interestDaysCount() {
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
