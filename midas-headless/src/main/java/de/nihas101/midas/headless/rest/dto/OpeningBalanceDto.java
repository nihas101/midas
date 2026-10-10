package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpeningBalanceDto {
    private Integer id;
    private Integer shareholderId;
    private MoneyAmount openingBalance;
    private Year year;
    private Source source;
}
