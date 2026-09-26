package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.MoneyAmount;
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
    private MoneyAmount amount;
    private boolean hidden;
}
