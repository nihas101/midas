package de.nihas101.midas.interest.row;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Locale;

@RequiredArgsConstructor
public class InterestRow implements InterestCalculationRow {
    private final MoneyAmount interest;
    private final String label;

    public InterestRow(
            final MoneyAmount interest,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                interest,
                messageSource.getMessage(
                        "interest.summary.interest",
                        null,
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
