package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.shareholders.dto.Shareholder;

import java.math.BigDecimal;
import java.util.List;

public record PdfViewData(
        ExportViewName viewName,
        String shareholderName,
        Shareholder shareholder, // TODO: Create a separate class for this instead of reusing shareholder
        Integer year,
        BigDecimal interestRate,
        List<String> headers,
        List<Object> rows
) {
}
