package de.nihas101.midas.interest.row;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@RequiredArgsConstructor
public class DivisorRow implements InterestCalculationRow {
    private final BigDecimal divisor;
    private final String label;

    public DivisorRow(
            final BigDecimal divisor,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                divisor,
                messageSource.getMessage("interest.summary.divisor", null, locale)
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
        return divisor.setScale(2, RoundingMode.HALF_UP);
    }
}
