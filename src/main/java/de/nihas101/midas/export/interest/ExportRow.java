package de.nihas101.midas.export.interest;

import de.nihas101.midas.export.sort.SortableExportRow;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExportRow(
        Integer shareholderId,
        String shareholderName,
        LocalDate date,
        BigDecimal transactions,
        String transSH,
        BigDecimal balance,
        String balanceSH,
        Integer days,
        BigDecimal interestNumber,
        BigDecimal rate
) implements SortableExportRow {

    @Override
    public Integer id() {
        return 0;
    }
}
