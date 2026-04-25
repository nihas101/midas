package de.nihas101.midas.export;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.export.accountstatement.AccountStatementExportDataSource;
import de.nihas101.midas.export.accountstatement.AccountStatementsRowExtractor;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountStatementExportDataSourceTest {

    @Mock
    private AccountStatementService accountStatementService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ExportTarget exportTarget;

    private AccountStatementExportDataSource underTest;

    private final Locale locale = Locale.GERMAN;
    private final LocalDate startDate = LocalDate.of(2023, 1, 1);
    private final LocalDate endDate = LocalDate.of(2023, 12, 31);
    private final Shareholder alice = new Shareholder(1, 1, "Alice", "A");
    private final Shareholder bob = new Shareholder(2, 2, "Bob", "B");

    @BeforeEach
    void setUp() {
        underTest = new AccountStatementExportDataSource(
                new AccountStatementsRowExtractor(
                        List.of(alice, bob),
                        startDate,
                        endDate,
                        accountStatementService,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );

        // Default mock for headers and sheet names to avoid NPE in List.of
        lenient().when(messageSource.getMessage(any(), any(), any()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void export_callsTargetWithCorrectData() {
        // Given
        RunningTotalAccountStatements aliceStatements = mock(RunningTotalAccountStatements.class);
        RunningTotalAccountStatement aliceRow = mock(RunningTotalAccountStatement.class);
        when(aliceRow.date()).thenReturn(LocalDate.of(2023, 5, 10));
        when(aliceRow.amount()).thenReturn(MoneyAmount.of(new BigDecimal("-50.00")));
        when(aliceRow.currentBalance()).thenReturn(MoneyAmount.of(new BigDecimal("100.00")));
        when(aliceRow.label()).thenReturn("Withdrawal");
        when(aliceRow.id()).thenReturn(101);
        when(aliceStatements.runningTotalAccountStatements()).thenReturn(List.of(aliceRow));

        RunningTotalAccountStatements bobStatements = mock(RunningTotalAccountStatements.class);
        RunningTotalAccountStatement bobRow = mock(RunningTotalAccountStatement.class);
        when(bobRow.date()).thenReturn(LocalDate.of(2023, 6, 15));
        when(bobRow.amount()).thenReturn(MoneyAmount.of(new BigDecimal("200.00")));
        when(bobRow.currentBalance()).thenReturn(MoneyAmount.of(new BigDecimal("300.00")));
        when(bobRow.label()).thenReturn("Interest");
        when(bobRow.id()).thenReturn(102);
        when(bobStatements.runningTotalAccountStatements()).thenReturn(List.of(bobRow));

        when(accountStatementService.runningTotalAccountStatements(eq(alice), eq(Year.of(2023)), eq(messageSource), eq(locale)))
                .thenReturn(aliceStatements);
        when(accountStatementService.runningTotalAccountStatements(eq(bob), eq(Year.of(2023)), eq(messageSource), eq(locale)))
                .thenReturn(bobStatements);

        // When
        underTest.export(exportTarget);

        // Then
        ArgumentCaptor<List<List<Object>>> rowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(exportTarget).export(eq("account-statements"), any(), rowsCaptor.capture());

        List<List<Object>> rows = rowsCaptor.getValue();
        assertEquals(2, rows.size());

        // Verify Alice's row (Sorted by name)
        List<Object> row1 = rows.get(0);
        assertEquals("Alice A", row1.get(0));
        assertEquals(101, row1.get(1));
        assertEquals(LocalDate.of(2023, 5, 10), row1.get(2));
        assertEquals("Withdrawal", row1.get(3));
        // Use compareTo for BigDecimals to ignore scale differences
        assertEquals(0, ((BigDecimal) row1.get(4)).compareTo(new BigDecimal("50.00"))); // Debit
        assertEquals(0, ((BigDecimal) row1.get(5)).compareTo(new BigDecimal("0.00")));  // Credit
        assertEquals(0, ((BigDecimal) row1.get(6)).compareTo(new BigDecimal("100.00"))); // Balance

        // Verify Bob's row
        List<Object> row2 = rows.get(1);
        assertEquals("Bob B", row2.get(0));
        assertEquals(0, ((BigDecimal) row2.get(4)).compareTo(new BigDecimal("0.00")));  // Debit
        assertEquals(0, ((BigDecimal) row2.get(5)).compareTo(new BigDecimal("200.00"))); // Credit
    }

    @Test
    void export_filtersByDateRange() {
        // Given
        underTest = new AccountStatementExportDataSource(
                new AccountStatementsRowExtractor(
                        List.of(alice),
                        LocalDate.of(2023, 6, 1),
                        LocalDate.of(2023, 6, 30),
                        accountStatementService,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );

        RunningTotalAccountStatements aliceStatements = mock(RunningTotalAccountStatements.class);
        RunningTotalAccountStatement rowIn = mock(RunningTotalAccountStatement.class);
        when(rowIn.date()).thenReturn(LocalDate.of(2023, 6, 15));
        when(rowIn.amount()).thenReturn(MoneyAmount.ZERO);
        when(rowIn.currentBalance()).thenReturn(MoneyAmount.ZERO);

        RunningTotalAccountStatement rowOut = mock(RunningTotalAccountStatement.class);
        when(rowOut.date()).thenReturn(LocalDate.of(2023, 5, 15));

        when(aliceStatements.runningTotalAccountStatements()).thenReturn(List.of(rowIn, rowOut));
        when(accountStatementService.runningTotalAccountStatements(any(), any(), any(), any())).thenReturn(aliceStatements);

        // When
        underTest.export(exportTarget);

        // Then
        ArgumentCaptor<List<List<Object>>> rowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(exportTarget).export(any(), any(), rowsCaptor.capture());
        assertEquals(1, rowsCaptor.getValue().size());
    }

    @Test
    void export_withMultipleYears_iteratesCorrectly() {
        // Given
        underTest = new AccountStatementExportDataSource(
                new AccountStatementsRowExtractor(
                        List.of(alice),
                        LocalDate.of(2022, 12, 31),
                        LocalDate.of(2023, 1, 1),
                        accountStatementService,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );

        RunningTotalAccountStatements s2022 = mock(RunningTotalAccountStatements.class);
        when(s2022.runningTotalAccountStatements()).thenReturn(List.of());
        RunningTotalAccountStatements s2023 = mock(RunningTotalAccountStatements.class);
        when(s2023.runningTotalAccountStatements()).thenReturn(List.of());

        when(accountStatementService.runningTotalAccountStatements(eq(alice), eq(Year.of(2022)), any(), any())).thenReturn(s2022);
        when(accountStatementService.runningTotalAccountStatements(eq(alice), eq(Year.of(2023)), any(), any())).thenReturn(s2023);

        // When
        underTest.export(exportTarget);

        // Then
        verify(accountStatementService).runningTotalAccountStatements(eq(alice), eq(Year.of(2022)), any(), any());
        verify(accountStatementService).runningTotalAccountStatements(eq(alice), eq(Year.of(2023)), any(), any());
    }
}
