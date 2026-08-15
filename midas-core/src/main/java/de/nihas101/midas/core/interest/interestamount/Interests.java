package de.nihas101.midas.core.interest.interestamount;

import de.nihas101.midas.api.interest.Interest;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Map;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
public class Interests {

    private final Map<Month, Interest> interests;

    public BigDecimal interest() {
        return interestAmountSum().divide(interestDivisor(), RoundingMode.HALF_UP);
    }

    public BigDecimal interestAmountSum() {
        if (interests == null || interests.isEmpty()) {
            return ZERO;
        }

        return interests.values().stream()
                .map(Interest::interestAmount)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal interestDivisor() {
        if (interests == null || interests.isEmpty()) {
            return ONE;
        }

        return interests.values()
                .stream()
                .findFirst()
                .map(Interest::interestDivisor)
                .orElse(ONE);
    }
}
