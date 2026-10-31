package de.nihas101.midas.headless.rest.dto;

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
public class ResponseLock {

    @NotNull
    @Positive
    @Schema(description = "Shareholder ID", example = "1")
    private Integer shareholderId;

    @NotNull
    @Schema(description = "Year", example = "2026")
    private Year year;

    @Schema(description = "Lock status", example = "true")
    private boolean locked;
}
