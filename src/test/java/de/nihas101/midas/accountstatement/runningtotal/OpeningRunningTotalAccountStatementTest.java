package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Locale;
import java.util.stream.Stream;

class OpeningRunningTotalAccountStatementTest {

    private static final Year TEST_YEAR = Year.of(2026);

    @Test
    void nullTest() {
        final OpeningRunningTotalAccountStatement statement = new OpeningRunningTotalAccountStatement(
                null,
                Mockito.mock(MessageSource.class),
                Locale.ENGLISH
        );
        Assertions.assertThrows(NullPointerException.class, statement::currentBalance);
        Assertions.assertThrows(NullPointerException.class, statement::date);
    }

    @ParameterizedTest
    @MethodSource("openingBalanceArguments")
    void implementationTests(final OpeningBalance openingBalance) {
        final MessageSource messageSource = Mockito.mock(MessageSource.class);
        Mockito.when(messageSource.getMessage(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("Balance");
        final OpeningRunningTotalAccountStatement statement = new OpeningRunningTotalAccountStatement(
                openingBalance,
                messageSource,
                Locale.ENGLISH
        );

        Assertions.assertEquals(openingBalance.getOpeningBalance(), statement.currentBalance());
        Assertions.assertEquals(openingBalance.getOpeningBalance(), statement.amount());
        Assertions.assertNull(statement.id());

        final LocalDate expectedDate = openingBalance.getYear().atMonth(Month.JANUARY).atDay(1);
        Assertions.assertEquals(expectedDate, statement.date());

        Assertions.assertEquals("Balance", statement.label());
    }

    public static Stream<Arguments> openingBalanceArguments() {
        return Stream.of(
                Arguments.of(new OpeningBalance(null, 1, MoneyAmount.ofCents(100L), TEST_YEAR)),
                Arguments.of(new OpeningBalance(null, 2, MoneyAmount.ZERO, TEST_YEAR)),
                Arguments.of(new OpeningBalance(null, 3, MoneyAmount.ofCents(-50L), TEST_YEAR.plusYears(1)))
        );
    }
}
