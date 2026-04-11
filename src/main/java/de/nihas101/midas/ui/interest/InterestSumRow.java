package de.nihas101.midas.ui.interest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@RequiredArgsConstructor
public class InterestSumRow implements InterestCalculationRow {
    private final BigDecimal interestSum;
    private final String label;

    public InterestSumRow(
            final BigDecimal interestSum,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                interestSum,
                messageSource.getMessage("interest.summary.interest-sum", null, locale)
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
        return null;
    }

    @Override
    public Integer interestDaysCount() {
        return null;
    }

    @Override
    public BigDecimal interestAmount() {
        return interestSum.setScale(0, RoundingMode.HALF_UP);
    }
}
