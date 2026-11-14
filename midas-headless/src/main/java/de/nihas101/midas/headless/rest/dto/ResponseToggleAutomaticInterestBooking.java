package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Status of automatic interest booking generation")
public class ResponseToggleAutomaticInterestBooking {

    @Schema(description = "Whether automatic interest booking generation is active", example = "true")
    private boolean active;
}
