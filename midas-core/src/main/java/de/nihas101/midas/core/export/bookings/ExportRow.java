package de.nihas101.midas.core.export.bookings;

import de.nihas101.midas.core.export.sort.SortableExportRow;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExportRow(
        Integer shareholderId,
        String shareholderName,
        Integer id,
        LocalDate date,
        String comment,
        String typeName,
        BigDecimal amount
) implements SortableExportRow {
}
