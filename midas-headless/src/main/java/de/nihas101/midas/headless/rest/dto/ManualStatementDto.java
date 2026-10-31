package de.nihas101.midas.headless.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualStatementDto {
    private Integer id;
    private String labelOverride;
    private MoneyAmountDto amount;
}
