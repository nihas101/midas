package de.nihas101.midas.export.bookings;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExportRow(
        String shareholderName,
        Integer id,
        LocalDate date,
        String comment,
        String typeName,
        BigDecimal amount
) {
}
