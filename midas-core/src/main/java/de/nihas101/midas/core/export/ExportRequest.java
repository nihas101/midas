package de.nihas101.midas.core.export;

import de.nihas101.midas.api.export.ExportFormat;
import de.nihas101.midas.api.shareholder.Shareholder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record ExportRequest(
        List<Shareholder> shareholders,
        ExportViews views,
        LocalDate startDate,
        LocalDate endDate,
        Set<ExportFormat> formats
) {

    public ExportRequest(
            List<Shareholder> shareholders,
            ExportViews views,
            LocalDate startDate,
            LocalDate endDate,
            ExportFormat formats
    ) {
        this(
                shareholders,
                views,
                startDate,
                endDate,
                Set.of(formats)
        );
    }
}
