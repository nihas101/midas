package de.nihas101.midas.core.interest.interestamount;

import de.nihas101.midas.api.interest.Interest;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

// https://de.wikipedia.org/wiki/Zinszahlen
// Using the 'kaufmaennische Zinsformel'
@ToString
@RequiredArgsConstructor
public class DefaultInterest implements Interest {

    private final BigDecimal capital; // aka 'Kapital' k
    private final BigDecimal interestDays; // aka 'Zinstage' t
    private final BigDecimal interestRate; // aka 'Zinsfuß' p

    public DefaultInterest(
            MoneyAmount capital,
            BigDecimal interestDays,
            BigDecimal interestRate
    ) {
        this(
                (capital != null ? capital.toBigDecimal() : null) != null ? capital.toBigDecimal() : BigDecimal.ZERO,
                interestDays != null && interestDays.longValue() >= 0 ? interestDays : BigDecimal.ZERO,
                interestRate != null && interestRate.doubleValue() > 0 ? interestRate : BigDecimal.ONE
        );
    }

    // aka 'Tageszins' Z
    @Override
    public BigDecimal dailyInterestRate() {
        return interestAmount().divide(interestDivisor(), RoundingMode.HALF_UP);
    }

    // aka 'Zins-' or 'Diskontzahl'
    @Override
    public BigDecimal interestAmount() {
        if (capital == null || interestDays == null) {
            return BigDecimal.ZERO;
        }

        return (capital.multiply(interestDays))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    // aka 'Zinsteiler'
    @Override
    public BigDecimal interestDivisor() {
        if (interestRate == null) {
            return BigDecimal.ONE;
        }

        return BigDecimal.valueOf(360).divide(interestRate, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal interestDays() {
        return interestDays;
    }
}
