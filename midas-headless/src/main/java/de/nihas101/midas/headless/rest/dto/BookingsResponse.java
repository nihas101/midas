package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingsResponse {
    @Schema(description = "Opening balance for this shareholder and year")
    private ResponseOpeningBalance openingBalance;
    @Schema(description = "List of bookings for this shareholder and year")
    private List<ResponseBooking> bookings;
}
