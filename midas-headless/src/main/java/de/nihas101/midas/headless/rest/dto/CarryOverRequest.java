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
public class CarryOverRequest {

    @NotNull
    @Positive
    @Schema(description = "Shareholder ID", example = "1")
    private Integer shareholderId;

    @NotNull
    @Schema(description = "Source year whose closing balance should be carried over", example = "2026")
    private Year fromYear;

    @Schema(description = "Whether to force overwrite an existing user-defined opening balance", example = "false")
    private boolean forceOverwrite;

    @Schema(description = "Carry over status (true to carry over, false to detach)", example = "true")
    private boolean active;
}
