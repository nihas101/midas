package de.nihas101.midas.export.xlsx;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class XslxFile {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public String name() {
        return "export_%s_%s.xlsx".formatted(
                startDate != null ? startDate.toString() : "",
                endDate != null ? endDate.toString() : ""
        );
    }

    public String mimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }
}