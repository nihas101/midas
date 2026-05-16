package de.nihas101.midas.interest.interestamount;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Map;

// TODO: Use for calculation and display
@RequiredArgsConstructor
public class Interests { // TODO: Think about using something else than BigDecimal here

    private final Map<Month, Interest> interests;

    public BigDecimal interest() {
        return interestAmountSum().divide(interestDivisor(), RoundingMode.HALF_UP);
    }

    public BigDecimal interestAmountSum() {
        return interests.values().stream()
                .map(Interest::interestAmount)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    public BigDecimal interestDivisor() {
        return interests.values()
                .stream()
                .findFirst()
                .map(Interest::interestDivisor)
                .orElse(BigDecimal.ONE);
    }
}
