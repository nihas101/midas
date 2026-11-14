package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.interest.InterestRate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInterestRate {

    @NotNull
    @Positive
    @Schema(
            description = "Interest rate as a decimal (e.g. 5.00 for 5%)",
            example = "5.00"
    )
    private BigDecimal interestRate;

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

    public static ResponseInterestRate toDto(final InterestRate rate) {
        return new ResponseInterestRate(
                rate.getInterestRate(),
                rate.getShareholderId(),
                rate.getYear()
        );
    }
}
