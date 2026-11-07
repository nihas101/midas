package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualStatementDto {
    @Schema(description = "Manual statement ID", example = "3")
    private Integer id;
    @Schema(description = "Label for the booking", example = "Adjusted deposit")
    private String label;
    @Schema(description = "Override amount")
    private MoneyAmountDto amount;
}
