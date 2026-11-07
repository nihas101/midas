package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestResponse {

    @NotNull
    @Positive
    @Schema(
            description = "Shareholder ID",
            example = "1"
    )
    private Integer shareholderId;

    @NotNull
    @Schema(
            description = "Year the interest rate applies to",
            example = "2026"
    )
    private Year year;

    @NotNull
    @Positive
    @Schema(
            description = "Interest rate as a decimal (e.g. 0.05 for 5%)",
            example = "0.05"
    )
    private BigDecimal interestRate;

    @Schema(description = "Calculated interest rows for each booking period")
    private List<ResponseInterestRow> rows;
}
