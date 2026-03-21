package de.nihas101.midas.interest.interestamount;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

// https://de.wikipedia.org/wiki/Zinszahlen
// Using the 'kaufmaennische Zinsformel'
@ToString
@RequiredArgsConstructor
public class Interest {

    private final BigDecimal capital; // aka 'Kapital' k
    private final BigDecimal interestDays; // aka 'Zinstage' t
    private final BigDecimal interestRate; // aka 'Zinsfuß' p

    // TODO: Use appropriate classes here instead of BigDecimal
    public Interest(
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
    public BigDecimal dailyInterestRate() {
        return interestAmount().divide(interestDivisor(), RoundingMode.HALF_UP);
    }

    // aka 'Zins-' or 'Diskontzahl'
    public BigDecimal interestAmount() {
        if (capital == null || interestDays == null) {
            return BigDecimal.ZERO;
        }

        return (capital.multiply(interestDays))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    // aka 'Zinsteiler'
    public BigDecimal interestDivisor() {
        if (interestRate == null) {
            return BigDecimal.ONE;
        }

        return BigDecimal.valueOf(360).divide(interestRate, RoundingMode.HALF_UP);
    }

    public BigDecimal interestDays() {
        return interestDays;
    }
}
