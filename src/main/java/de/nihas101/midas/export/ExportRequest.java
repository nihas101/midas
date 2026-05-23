package de.nihas101.midas.export;

import de.nihas101.midas.shareholders.dto.Shareholder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record ExportRequest(
        List<Shareholder> shareholders,
        ExportViews views,
        LocalDate startDate,
        LocalDate endDate,
        Set<String> formats
) {
}
