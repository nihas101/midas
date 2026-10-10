package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.interest.InterestCalculationRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestResponse {
    private InterestRateDto interestRate;
    private List<InterestCalculationRow> rows;
}
