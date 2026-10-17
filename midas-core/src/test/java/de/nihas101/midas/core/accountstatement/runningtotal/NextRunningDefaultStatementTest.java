package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatement;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NextRunningDefaultStatementTest {

    @Test
    void nextRunningBalanceNullBalance() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        null,
                        "dummy"
                ),
                MoneyAmount.ZERO
        );

        assertEquals(0, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }

    @Test
    void nextRunningBalanceNullAmount() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ZERO,
                        "dummy"
                ),
                null
        );

        assertEquals(0, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }

    @Test
    void nextRunningBalanceZeroes() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ZERO,
                        "dummy"
                ),
                MoneyAmount.ZERO
        );

        assertEquals(0, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }

    @Test
    void nextRunningZeroBalance() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ZERO,
                        "dummy"
                ),
                MoneyAmount.ofCents(100L)
        );

        assertEquals(100, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }

    @Test
    void nextRunningZeroAmount() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(110L),
                        "dummy"
                ),
                MoneyAmount.ZERO
        );

        assertEquals(110, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }

    @Test
    void nextRunningStatementPositive() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(110L),
                        "dummy"
                ),
                MoneyAmount.ofCents(90L)
        );

        assertEquals(200, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }

    @Test
    void nextRunningStatementNegative() {
        final NextRunningDefaultStatement nextRunningDefaultStatement = new NextRunningDefaultStatement(
                new DefaultAccountStatement(
                        1,
                        Year.of(2026),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(110L),
                        "dummy"
                ),
                MoneyAmount.ofCents(-10L)
        );

        assertEquals(100, nextRunningDefaultStatement.nextRunningTotal().currentBalance().getCents());
    }
}