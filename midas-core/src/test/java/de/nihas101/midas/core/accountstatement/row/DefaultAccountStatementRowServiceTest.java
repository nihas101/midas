package de.nihas101.midas.core.accountstatement.row;

import de.nihas101.midas.api.accountstatement.AccountStatementRow;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.core.config.AccountStatementConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAccountStatementRowServiceTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private RunningTotalAccountStatements runningTotalAccountStatements;

    @Mock
    private RunningTotalAccountStatement runningTotalAccountStatement;

    @Test
    void usesCustomDateFormatFromConfig() {
        final AccountStatementConfig config = new AccountStatementConfig("yyyy-MM-dd");
        final DefaultAccountStatementRowService service = new DefaultAccountStatementRowService(messageSource, config);

        when(runningTotalAccountStatement.date()).thenReturn(LocalDate.of(2026, 8, 15));
        when(runningTotalAccountStatements.runningTotalAccountStatements()).thenReturn(List.of(runningTotalAccountStatement));

        final List<AccountStatementRow> rows = service.generateRows(runningTotalAccountStatements, true);

        assertEquals(1, rows.size());
        assertEquals("2026-08-15", rows.get(0).dateStr());
    }

    @Test
    void usesDefaultDateFormatWhenConfigIsNull() {
        final DefaultAccountStatementRowService service = new DefaultAccountStatementRowService(messageSource, null);

        when(runningTotalAccountStatement.date()).thenReturn(LocalDate.of(2026, 8, 15));
        when(runningTotalAccountStatements.runningTotalAccountStatements()).thenReturn(List.of(runningTotalAccountStatement));

        final List<AccountStatementRow> rows = service.generateRows(runningTotalAccountStatements, true);

        assertEquals(1, rows.size());
        assertEquals("15.08", rows.get(0).dateStr());
    }
}
