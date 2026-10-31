package de.nihas101.midas.headless.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInterestRate {

    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    @Positive
    private BigDecimal interestRate;

    @NotNull
    private Year year;

}
