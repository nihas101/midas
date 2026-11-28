package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to create a manual extra entry on an account statement.")
public class RequestCreateManualExtra {

    @Schema(description = "ID of the shareholder", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private Integer shareholderId;

    @Schema(description = "Target year", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Year year;

    @Schema(description = "Display label for the manual extra entry", example = "Bonus")
    private String label;

    @Schema(description = "Amount for the manual extra entry", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private MoneyAmountDto amount;
}
