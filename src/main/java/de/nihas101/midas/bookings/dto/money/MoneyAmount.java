package de.nihas101.midas.bookings.dto.money;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class MoneyAmount {

    private static final BigDecimal MULTIPLIER = new BigDecimal("100");
    public static final MoneyAmount ZERO = new MoneyAmount(0L);

    private Long cents;

    public static MoneyAmount of(BigDecimal amount) {
        if (amount == null) {
            return ZERO;
        }
        return new MoneyAmount(amount.multiply(MULTIPLIER).setScale(0, RoundingMode.HALF_UP).longValue());
    }

    public static MoneyAmount ofCents(Long cents) {
        return new MoneyAmount(cents == null ? 0L : cents);
    }

    public MoneyAmount plus(MoneyAmount other) {
        if (other == null) {
            return this;
        }
        return new MoneyAmount(this.cents + other.cents);
    }

    public MoneyAmount minus(MoneyAmount other) {
        if (other == null) {
            return this;
        }
        return new MoneyAmount(this.cents - other.cents);
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(cents).divide(MULTIPLIER, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal toBigDecimalForInput() {
        return new BigDecimal(cents).divide(MULTIPLIER, 2, RoundingMode.HALF_UP);
    }

    public String format(Locale locale) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        // Ensure consistent formatting regardless of locale defaults if needed,
        // but usually we want the locale's style.
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(toBigDecimal());
    }

    @Override
    public String toString() {
        return format(Locale.ENGLISH); // Default for internal logging/debugging
    }
}
