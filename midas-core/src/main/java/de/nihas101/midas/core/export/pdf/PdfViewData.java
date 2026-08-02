package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.ExportViewName;

import java.math.BigDecimal;
import java.util.List;

public record PdfViewData(
        ExportViewName viewName,
        String shareholderName,
        Shareholder shareholder,
        Integer year,
        BigDecimal interestRate,
        List<String> headers,
        List<Object> rows
) {
}
