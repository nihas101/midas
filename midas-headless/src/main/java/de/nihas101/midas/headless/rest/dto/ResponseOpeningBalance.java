package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.Source;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Opening balance ID", example = "5")
    private Integer id;

    @NotNull
    @Positive
    @Schema(description = "Shareholder ID", example = "1")
    private Integer shareholderId;

    @NotNull
    @Schema(description = "Opening balance amount")
    private MoneyAmountDto openingBalance;

    @NotNull
    @Schema(description = "Year", example = "2026")
    private Year year;

    @Schema(description = "Source of this opening balance", example = "USER")
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
