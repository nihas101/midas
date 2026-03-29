package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatement;
import de.nihas101.midas.accountstatement.dto.DefaultAccountStatement;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;
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
        Assertions.assertNull(runningTotal.label(null, null));
    }

    @ParameterizedTest
    @MethodSource("delegationArguments")
    void delegationTests(
            final AccountStatement statement,
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
                        new DefaultAccountStatement(1, TEST_YEAR, BookingType.WITHDRAWAL, MoneyAmount.ofCents(100L)),
                        MoneyAmount.ofCents(500L)
                ),
                Arguments.of(
                        new DefaultAccountStatement(null, TEST_YEAR, BookingType.INTEREST, MoneyAmount.ZERO),
                        MoneyAmount.ofCents(-100L)
                ),
                Arguments.of(
                        new DefaultAccountStatement(99, TEST_YEAR, BookingType.COMPENSATION, MoneyAmount.ofCents(100000L)),
                        MoneyAmount.ZERO
                )
        );
    }

    @Test
    void labelDelegation() {
        final AccountStatement statement = mock(AccountStatement.class);
        final MessageSource messageSource = mock(MessageSource.class);
        final Locale locale = Locale.GERMAN;
        final String expectedLabel = "Test Label";

        when(statement.label(messageSource, locale)).thenReturn(expectedLabel);

        final DefaultRunningTotalAccountStatement runningTotal = new DefaultRunningTotalAccountStatement(statement, TEST_BALANCE);

        Assertions.assertEquals(expectedLabel, runningTotal.label(messageSource, locale));
    }
}
