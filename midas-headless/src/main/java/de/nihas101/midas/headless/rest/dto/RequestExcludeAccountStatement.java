package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to exclude or include a booking type in an account statement.")
public class RequestExcludeAccountStatement {
    @Schema(description = "ID of the shareholder", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer shareholderId;
    @Schema(description = "Target year", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Year year;
    @Schema(description = "Booking type to exclude or include", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private BookingType bookingType;
    @Schema(description = "Whether to exclude the booking type", example = "true")
    private boolean exclude;
}
