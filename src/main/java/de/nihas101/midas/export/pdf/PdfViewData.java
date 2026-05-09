package de.nihas101.midas.export.pdf;

import java.util.List;

public record PdfViewData(
        String viewName,
        String shareholderName,
        List<String> headers,
        List<Object> rows
) {
}
