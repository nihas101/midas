package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.DefaultAccountStatement;
import de.nihas101.midas.accountstatement.dto.LabeledAccountStatement;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultRunningTotalAccountStatementTest {

    private static final Year TEST_YEAR = Year.of(2026);
    private static final MoneyAmount TEST_BALANCE = MoneyAmount.ofCents(12345L);

    @Test
    void nullTest() {
        final DefaultRunningTotalAccountStatement runningTotal = new DefaultRunningTotalAccountStatement(null, null);
        Assertions.assertNull(runningTotal.id());
        Assertions.assertNull(runningTotal.currentBalance());
        Assertions.assertNull(runningTotal.amount());
        Assertions.assertNull(runningTotal.date());
        Assertions.assertNull(runningTotal.label());
    }

    @ParameterizedTest
    @MethodSource("delegationArguments")
    void delegationTests(
            final LabeledAccountStatement statement,
            final MoneyAmount currentBalance
    ) {
        final DefaultRunningTotalAccountStatement runningTotal = new DefaultRunningTotalAccountStatement(statement, currentBalance);

        Assertions.assertEquals(statement.id(), runningTotal.id());
        Assertions.assertEquals(statement.date(), runningTotal.date());
        Assertions.assertEquals(statement.amount(), runningTotal.amount());
        Assertions.assertEquals(currentBalance, runningTotal.currentBalance());
    }

    public static Stream<Arguments> delegationArguments() {
        return Stream.of(
                Arguments.of(
                        new DefaultAccountStatement(
                                1,
                                TEST_YEAR,
                                BookingType.WITHDRAWAL,
                                MoneyAmount.ofCents(100L),
                                null,
                                null
                        ),
                        MoneyAmount.ofCents(500L)
                ),
                Arguments.of(
                        new DefaultAccountStatement(
                                null,
                                TEST_YEAR,
                                BookingType.INTEREST,
                                MoneyAmount.ZERO,
                                null,
                                null
                        ),
                        MoneyAmount.ofCents(-100L)
                ),
                Arguments.of(
                        new DefaultAccountStatement(
                                99,
                                TEST_YEAR,
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100000L),
                                null,
                                null
                        ),
                        MoneyAmount.ZERO
                )
        );
    }

    @Test
    void labelDelegation() {
        final LabeledAccountStatement statement = mock(LabeledAccountStatement.class);
        final String expectedLabel = "Test Label";

        when(statement.label()).thenReturn(expectedLabel);

        final DefaultRunningTotalAccountStatement runningTotal = new DefaultRunningTotalAccountStatement(statement, TEST_BALANCE);

        Assertions.assertEquals(expectedLabel, runningTotal.label());
    }
}
