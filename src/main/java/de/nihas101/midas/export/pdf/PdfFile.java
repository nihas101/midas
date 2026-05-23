package de.nihas101.midas.export.pdf;

import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class PdfFile {

    private final Shareholder shareholder;
    private final String view;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public String name() {
        return "%s_%s_(%s-%s)_%s_%s_%s.pdf".formatted(
                shareholder.getFirstName() != null ? shareholder.getFirstName() : "",
                shareholder.getLastName() != null ? shareholder.getLastName() : "",
                shareholder.getDisplayId() != null ? shareholder.getDisplayId() : "",
                // We include the id here to guarantee the filename is unique
                shareholder.getId() != null ? shareholder.getId() : "",
                view != null ? view : "",
                startDate != null ? startDate.toString() : "",
                endDate != null ? endDate.toString() : ""
        );
    }

    public String mimeType() {
        return "application/pdf";
    }
}