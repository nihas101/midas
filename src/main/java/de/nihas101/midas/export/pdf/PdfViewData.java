package de.nihas101.midas.export.pdf;

import de.nihas101.midas.shareholders.dto.Shareholder;

import java.math.BigDecimal;
import java.util.List;

public record PdfViewData(
        String viewName,
        String shareholderName,
        Shareholder shareholder,
        Integer year,
        BigDecimal interestRate,
        List<String> headers,
        List<Object> rows
) {
}
