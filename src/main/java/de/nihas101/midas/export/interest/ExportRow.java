package de.nihas101.midas.export.interest;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExportRow(
        String shareholderName,
        LocalDate date,
        BigDecimal transactions,
        String transSH,
        BigDecimal balance,
        String balanceSH,
        Integer days,
        BigDecimal interestNumber,
        BigDecimal rate
) {
}
