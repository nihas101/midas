package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementsResponse {
    @Schema(description = "All account statements for the shareholder and year (bookings + manual extras)")
    private List<AccountStatementDto> accountStatements;
    @Schema(description = "Manual statement overrides (label / amount overrides)")
    private List<ManualStatementDto> overrides;
}
