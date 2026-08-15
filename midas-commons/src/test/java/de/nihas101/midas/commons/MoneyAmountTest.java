package de.nihas101.midas.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyAmountTest {

    @Test
    void absReturnsPositiveAmount() {
        final MoneyAmount negative = MoneyAmount.ofCents(-500L);
        final MoneyAmount zero = MoneyAmount.ZERO;
        final MoneyAmount positive = MoneyAmount.ofCents(500L);

        assertEquals(MoneyAmount.ofCents(500L), positive.abs());
        assertEquals(MoneyAmount.ofCents(500L), negative.abs());
        assertEquals(MoneyAmount.ZERO, zero.abs());
    }

    @Test
    void smallerThanComparesAmountsCorrectly() {
        final MoneyAmount a = MoneyAmount.ofCents(100L);
        final MoneyAmount b = MoneyAmount.ofCents(200L);

        assertTrue(a.smallerThan(b));
        assertFalse(b.smallerThan(a));
        assertFalse(a.smallerThan(a));
    }
}
