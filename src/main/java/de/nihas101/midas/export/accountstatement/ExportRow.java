package de.nihas101.midas.export.accountstatement;

import de.nihas101.midas.export.sort.SortableExportRow;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExportRow(
        Integer shareholderId,
        String shareholderName,
        Integer id,
        LocalDate date,
        String type,
        BigDecimal debit,
        BigDecimal credit,
        BigDecimal balance
) implements SortableExportRow {
}
