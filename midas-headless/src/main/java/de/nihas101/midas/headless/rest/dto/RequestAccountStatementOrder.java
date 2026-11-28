package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to update the display order of account statement rows.")
public class RequestAccountStatementOrder {
    @Schema(description = "ID of the shareholder", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private Integer shareholderId;

    @Schema(description = "Target year", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Year year;

    @Schema(description = "Ordered list of row keys", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    private List<String> rowKeys;
}
