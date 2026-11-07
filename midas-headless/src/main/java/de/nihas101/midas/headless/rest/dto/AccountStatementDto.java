package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto {

    @Schema(description = "Booking type", example = "DEPOSIT")
    private BookingType type;
    @Schema(description = "Date of the statement", example = "2026-03-15")
    private LocalDate date;
    @Schema(description = "Amount")
    private MoneyAmountDto amount;
    @Schema(description = "Label / description", example = "Quarterly deposit")
    private String label;
    @Schema(description = "Whether this entry is hidden", example = "false")
    private boolean hidden;
    @Schema(description = "Whether this is a manual extra entry", example = "false")
    private boolean manualExtra;
}
