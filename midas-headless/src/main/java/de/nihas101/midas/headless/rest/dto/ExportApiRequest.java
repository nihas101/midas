package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.core.export.ExportViewName;
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
    private List<Integer> shareholderIds;
    private Set<ExportViewName> views;
    private LocalDate from;
    private LocalDate until;
    private Set<String> formats; // e.g. "pdf", "xlsx"
}
