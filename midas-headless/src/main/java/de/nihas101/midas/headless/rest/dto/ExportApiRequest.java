package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.export.ExportFormat;
import de.nihas101.midas.core.export.ExportViewName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportApiRequest {

    @Schema(description = "List of shareholder IDs to export; omit or leave empty to export all", example = "[1, 2, 3]")
    private List<Integer> shareholderIds;

    @Schema(description = "Set of views to include in the export; omit or leave empty to include all")
    private Set<ExportViewName> views;

    @NotNull
    @Schema(description = "Start date of the export period (inclusive)", example = "2026-01-01")
    private LocalDate from;

    @NotNull
    @Schema(description = "End date of the export period (inclusive)", example = "2026-12-31")
    private LocalDate until;

    @NotNull
    @Schema(description = "Export file format", example = "PDF")
    private ExportFormat format;
}
