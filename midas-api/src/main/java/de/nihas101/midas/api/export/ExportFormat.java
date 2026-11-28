package de.nihas101.midas.api.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportFormat {

    XLSX("xlsx"),
    PDF("pdf");

    private final String messageKey;
}
