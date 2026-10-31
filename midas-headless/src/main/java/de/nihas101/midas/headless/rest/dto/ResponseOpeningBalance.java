package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.Source;
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
public class ResponseOpeningBalance {

    @NotNull
    @Positive
    private Integer id;

    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    private MoneyAmountDto openingBalance;

    @NotNull
    private Year year;

    private Source source;

    public static ResponseOpeningBalance toDto(final OpeningBalance openingBalance) {
        return ResponseOpeningBalance.builder()
                .id(openingBalance.getId())
                .shareholderId(openingBalance.getShareholderId())
                .openingBalance(MoneyAmountDto.fromDomain(openingBalance.getOpeningBalance()))
                .year(openingBalance.getYear())
                .source(openingBalance.getSource())
                .build();
    }
}
