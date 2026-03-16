package de.nihas101.midas.interest.interestamount;

import de.nihas101.midas.money.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

class InterestTest {

    private final Interest interest = new Interest(
            MoneyAmount.ofCents(1000000L),
            BigDecimal.valueOf(16),
            BigDecimal.valueOf(0.5)
    );

    @Test
    void dailyInterestRate_null() {
        Assertions.assertEquals(createExpected(0), new Interest(
                (MoneyAmount) null,
                null,
                null
        ).dailyInterestRate().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    void dailyInterestRate() {
        Assertions.assertEquals(createExpected(2.2222), interest.dailyInterestRate());
    }

    @Test
    void interestAmount_null() {
        Assertions.assertEquals(createExpected(0), new Interest(
                (MoneyAmount) null,
                null,
                null
        ).interestAmount().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    void interestAmount_interestDaysNegative() {
        Assertions.assertEquals(createExpected(0), new Interest(
                (MoneyAmount) null,
                BigDecimal.valueOf(-1),
                null
        ).interestAmount().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    void interestAmount() {
        Assertions.assertEquals(createExpected(1600), interest.interestAmount());
    }

    @Test
    void interestDivisor_null() {
        Assertions.assertEquals(createExpected(360), new Interest(
                (MoneyAmount) null,
                null,
                null
        ).interestDivisor().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    void interestDivisor_interestRate0() {
        Assertions.assertEquals(createExpected(360), new Interest(
                (MoneyAmount) null,
                null,
                BigDecimal.valueOf(0)
        ).interestDivisor().setScale(4, RoundingMode.HALF_UP));
    }

    @Test
    void interestDivisor() {
        Assertions.assertEquals(createExpected(720), interest.interestDivisor().setScale(4, RoundingMode.HALF_UP));
    }

    private BigDecimal createExpected(final double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal createExpected(final int value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }
}