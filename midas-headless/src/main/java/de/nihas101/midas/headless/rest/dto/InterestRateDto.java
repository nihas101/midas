package de.nihas101.midas.headless.rest.dto;

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
public class InterestRateDto {
    private Integer id;
    private Integer shareholderId;
    private BigDecimal interestRate;
    private Year year;
    private boolean updateInterest;
}
