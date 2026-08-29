package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatement;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultRunningTotalAccountStatementsTest {

    private static final Year TEST_YEAR = Year.of(2026);

    @Test
    void nullOpeningBalance() {
        final AccountStatements accountStatements = mock(AccountStatements.class);
        when(accountStatements.openingBalance()).thenReturn(null);

        final DefaultRunningTotalAccountStatements runningTotals = new DefaultRunningTotalAccountStatements(
                accountStatements,
                List.of(),
                new OpeningRunningTotalAccountStatement(
                        accountStatements.openingBalance(),
                        Mockito.mock(MessageSource.class),
                        Locale.ENGLISH,
                        TEST_YEAR
                )
        );

        Assertions.assertTrue(runningTotals.isEmpty());
        Assertions.assertEquals(0, runningTotals.runningTotalAccountStatements().size());
    }

    @Test
    void calculationTest() {
        // Arrange
        final OpeningBalance openingBalance = new DefaultOpeningBalance(null, null, MoneyAmount.ofCents(1000L), TEST_YEAR, Source.USER);

        final LabeledAccountStatement withdrawal = new DefaultAccountStatement(
                1,
                TEST_YEAR,
                BookingType.WITHDRAWAL,
                MoneyAmount.ofCents(-200L),
                null,
                null
        );
        final LabeledAccountStatement interest = new DefaultAccountStatement(
                2,
                TEST_YEAR,
                BookingType.INTEREST,
                MoneyAmount.ofCents(50L),
                null,
                null
        );

        final AccountStatements accountStatements = mock(AccountStatements.class);
        when(accountStatements.openingBalance()).thenReturn(openingBalance);
        when(accountStatements.forType(BookingType.WITHDRAWAL)).thenReturn(withdrawal);
        when(accountStatements.forType(BookingType.INTEREST)).thenReturn(interest);

        final List<BookingType> typeOrder = List.of(BookingType.WITHDRAWAL, BookingType.INTEREST);

        // Act
        final DefaultRunningTotalAccountStatements runningTotals = new DefaultRunningTotalAccountStatements(
                accountStatements,
                typeOrder,
                new OpeningRunningTotalAccountStatement(openingBalance, Mockito.mock(MessageSource.class), Locale.ENGLISH, TEST_YEAR)
        );

        // Assert
        Assertions.assertFalse(runningTotals.isEmpty());
        final List<RunningTotalAccountStatement> resultList = runningTotals.runningTotalAccountStatements();

        // 1 (Opening) + 2 (Types) = 3 rows
        Assertions.assertEquals(3, resultList.size());

        // Row 0: Opening Balance
        Assertions.assertEquals(MoneyAmount.ofCents(1000L), resultList.get(0).currentBalance());
        Assertions.assertEquals(MoneyAmount.ofCents(1000L), resultList.get(0).amount());

        // Row 1: Withdrawal (1000 - 200 = 800)
        Assertions.assertEquals(MoneyAmount.ofCents(800L), resultList.get(1).currentBalance());
        Assertions.assertEquals(MoneyAmount.ofCents(-200L), resultList.get(1).amount());
        Assertions.assertEquals(BookingType.WITHDRAWAL, resultList.get(1).id() != null ? BookingType.WITHDRAWAL : null);

        // Row 2: Interest (800 + 50 = 850)
        Assertions.assertEquals(MoneyAmount.ofCents(850L), resultList.get(2).currentBalance());
        Assertions.assertEquals(MoneyAmount.ofCents(50L), resultList.get(2).amount());
    }

    @Test
    void verifyOrder() {
        // Arrange
        final OpeningBalance openingBalance = new DefaultOpeningBalance(null, null, MoneyAmount.ZERO, TEST_YEAR, Source.USER);
        final AccountStatements accountStatements = mock(AccountStatements.class);
        when(accountStatements.openingBalance()).thenReturn(openingBalance);

        final LabeledAccountStatement withdrawal = new DefaultAccountStatement(
                1,
                TEST_YEAR,
                BookingType.WITHDRAWAL,
                MoneyAmount.ofCents(10L),
                null,
                null
        );
        final LabeledAccountStatement interest = new DefaultAccountStatement(
                2,
                TEST_YEAR,
                BookingType.INTEREST,
                MoneyAmount.ofCents(20L),
                null,
                null
        );

        when(accountStatements.forType(BookingType.WITHDRAWAL)).thenReturn(withdrawal);
        when(accountStatements.forType(BookingType.INTEREST)).thenReturn(interest);

        // Act & Assert for Order A
        final DefaultRunningTotalAccountStatements orderA = new DefaultRunningTotalAccountStatements(
                accountStatements,
                List.of(BookingType.WITHDRAWAL, BookingType.INTEREST),
                new OpeningRunningTotalAccountStatement(openingBalance, Mockito.mock(MessageSource.class), Locale.ENGLISH, TEST_YEAR)
        );
        Assertions.assertEquals(MoneyAmount.ofCents(10L), orderA.runningTotalAccountStatements().get(1).currentBalance());
        Assertions.assertEquals(MoneyAmount.ofCents(30L), orderA.runningTotalAccountStatements().get(2).currentBalance());

        // Act & Assert for Order B
        final DefaultRunningTotalAccountStatements orderB = new DefaultRunningTotalAccountStatements(
                accountStatements,
                List.of(BookingType.INTEREST, BookingType.WITHDRAWAL),
                new OpeningRunningTotalAccountStatement(openingBalance, Mockito.mock(MessageSource.class), Locale.ENGLISH, TEST_YEAR)
        );
        Assertions.assertEquals(MoneyAmount.ofCents(20L), orderB.runningTotalAccountStatements().get(1).currentBalance());
        Assertions.assertEquals(MoneyAmount.ofCents(30L), orderB.runningTotalAccountStatements().get(2).currentBalance());
    }

    @Test
    void manualStatementsCalculationTest() {
        // Arrange
        final OpeningBalance openingBalance = new DefaultOpeningBalance(null, null, MoneyAmount.ofCents(1000L), TEST_YEAR, Source.USER);

        final LabeledAccountStatement withdrawal = new DefaultAccountStatement(
                1,
                TEST_YEAR,
                BookingType.WITHDRAWAL,
                MoneyAmount.ofCents(-200L),
                "Entnahmen",
                false,
                Source.USER
        );
        final LabeledAccountStatement manualExtra = new DefaultAccountStatement(
                99,
                TEST_YEAR,
                null,
                MoneyAmount.ofCents(150L),
                "Zusatz",
                false,
                Source.USER
        );

        final AccountStatements accountStatements = mock(AccountStatements.class);
        when(accountStatements.openingBalance()).thenReturn(openingBalance);
        when(accountStatements.forType(BookingType.WITHDRAWAL)).thenReturn(withdrawal);
        when(accountStatements.manualStatements()).thenReturn(List.of(manualExtra));

        final List<BookingType> typeOrder = List.of(BookingType.WITHDRAWAL);

        // Act
        final DefaultRunningTotalAccountStatements runningTotals = new DefaultRunningTotalAccountStatements(
                accountStatements,
                typeOrder,
                new OpeningRunningTotalAccountStatement(openingBalance, Mockito.mock(MessageSource.class), Locale.ENGLISH, TEST_YEAR)
        );

        // Assert
        Assertions.assertFalse(runningTotals.isEmpty());
        final List<RunningTotalAccountStatement> resultList = runningTotals.runningTotalAccountStatements();

        // 1 (Opening) + 1 (Withdrawal) + 1 (Manual Extra) = 3 rows
        Assertions.assertEquals(3, resultList.size());

        // Row 0: Opening Balance
        Assertions.assertEquals(MoneyAmount.ofCents(1000L), resultList.get(0).currentBalance());

        // Row 1: Withdrawal (1000 - 200 = 800)
        Assertions.assertEquals(MoneyAmount.ofCents(800L), resultList.get(1).currentBalance());

        // Row 2: Manual Extra (800 + 150 = 950)
        Assertions.assertEquals(MoneyAmount.ofCents(950L), resultList.get(2).currentBalance());
        Assertions.assertEquals(MoneyAmount.ofCents(150L), resultList.get(2).amount());
        Assertions.assertEquals("Zusatz", resultList.get(2).label());
        Assertions.assertTrue(resultList.get(2).isManualExtra());
    }
}
