package de.nihas101.midas.export.interest;

import de.nihas101.midas.export.ExportTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestExportDataSourceTest {

    @Mock
    private InterestRowExtractor rowExtractor;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ExportTarget exportTarget;

    private InterestExportDataSource interestExportDataSource;

    private final Locale locale = Locale.GERMAN;

    @BeforeEach
    void setUp() {
        interestExportDataSource = new InterestExportDataSource(rowExtractor, messageSource, locale);
        // Default mock for headers and sheet names
        lenient().when(messageSource.getMessage(any(), any(), any()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void export_callsTargetWithFormattedData() {
        ExportRow row1 = new ExportRow(
                "Alice A",
                LocalDate.of(2023, 1, 31),
                new BigDecimal("100.00"),
                "H",
                new BigDecimal("1000.00"),
                "H",
                30,
                new BigDecimal("5"),
                new BigDecimal("2.5")
        );
        when(rowExtractor.rows()).thenReturn(List.of(row1));

        interestExportDataSource.export(exportTarget);

        ArgumentCaptor<List<List<Object>>> rowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(exportTarget).export(eq("interest-calculation"), any(), rowsCaptor.capture());

        List<List<Object>> resultRows = rowsCaptor.getValue();
        assertEquals(1, resultRows.size());

        List<Object> resultRow = resultRows.getFirst();
        assertEquals("Alice A", resultRow.get(0));
        assertEquals(LocalDate.of(2023, 1, 31), resultRow.get(1));
        assertEquals(new BigDecimal("100.00"), resultRow.get(2));
        assertEquals("H", resultRow.get(3));
        assertEquals(new BigDecimal("1000.00"), resultRow.get(4));
        assertEquals("H", resultRow.get(5));
        assertEquals(30, resultRow.get(6));
        assertEquals(new BigDecimal("5"), resultRow.get(7));
        assertEquals(new BigDecimal("2.5"), resultRow.get(8));
    }

    @Test
    void export_withEmptyRows_doesNotcallTarget() {
        when(rowExtractor.rows()).thenReturn(List.of());

        interestExportDataSource.export(exportTarget);

        verify(exportTarget, never()).export(any(), any(), eq(List.of()));
    }

    @Test
    void getHeaders_localizesAllColumns() {
        ExportRow row = new ExportRow(
                "Alice A",
                LocalDate.of(2023, 1, 31),
                new BigDecimal("100.00"),
                "H",
                new BigDecimal("1000.00"),
                "H",
                30,
                new BigDecimal("5"),
                new BigDecimal("2.5")
        );
        when(rowExtractor.rows()).thenReturn(List.of(row));
        interestExportDataSource.export(exportTarget);

        // Verify we localized the sheet name and headers
        verify(messageSource).getMessage(eq("interest-calculation"), any(), eq(locale));
        verify(messageSource).getMessage(eq("bookings.shareholder"), any(), eq(locale));
        verify(messageSource).getMessage(eq("interest.table.month"), any(), eq(locale));
        verify(messageSource).getMessage(eq("interest.table.transactions"), any(), eq(locale));
        verify(messageSource, atLeast(2)).getMessage(eq("interest.table.sh"), any(), eq(locale));
        verify(messageSource).getMessage(eq("interest.table.balance"), any(), eq(locale));
        verify(messageSource).getMessage(eq("interest.table.days"), any(), eq(locale));
        verify(messageSource).getMessage(eq("interest.table.interest-amount"), any(), eq(locale));
        verify(messageSource).getMessage(eq("interest.rate.label"), any(), eq(locale));
    }
}
