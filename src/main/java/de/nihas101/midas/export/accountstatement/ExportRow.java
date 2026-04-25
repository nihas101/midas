package de.nihas101.midas.export.accountstatement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExportRow(
        String shareholderName,
        Integer id,
        LocalDate date,
        String type,
        BigDecimal debit,
        BigDecimal credit,
        BigDecimal balance
) {
}
