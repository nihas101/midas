package de.nihas101.midas.interest.interestamount;

import de.nihas101.midas.money.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Map;

class InterestsTest {

    private final Interests interests = new Interests(
            Map.of(
                    Month.SEPTEMBER, new Interest(
                            MoneyAmount.ofCents(1000000L),
                            BigDecimal.valueOf(16),
                            BigDecimal.valueOf(0.5)
                    ),
                    Month.OCTOBER, new Interest(
                            MoneyAmount.ofCents(1360000L),
                            BigDecimal.valueOf(28),
                            BigDecimal.valueOf(0.5)
                    ),
                    Month.NOVEMBER, new Interest(
                            MoneyAmount.ofCents(1140000L),
                            BigDecimal.valueOf(46),
                            BigDecimal.valueOf(0.5)
                    ),
                    Month.DECEMBER, new Interest(
                            MoneyAmount.ofCents(1140000L),
                            BigDecimal.valueOf(0),
                            BigDecimal.valueOf(0.5)
                    )
            )
    );

    @Test
    void interestAmountSum() {
        Assertions.assertEquals(
                BigDecimal.valueOf(10652L).longValue(),
                interests.interestAmountSum().longValue()
        );
    }

    @Test
    void interestDivisor() {
        Assertions.assertEquals(
                BigDecimal.valueOf(720).longValue(),
                interests.interestDivisor().longValue()
        );
    }

    @Test
    void interest() {
        Assertions.assertEquals(
                BigDecimal.valueOf(14.79).setScale(2, RoundingMode.HALF_UP),
                interests.interest().setScale(2, RoundingMode.HALF_UP)
        );
    }

    // TODO: Tests for edge cases like null, empty map etc.
}