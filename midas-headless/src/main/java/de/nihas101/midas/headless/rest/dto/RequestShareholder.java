package de.nihas101.midas.headless.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestShareholder {

    @Positive
    @Schema(description = "Optional display ID of the shareholder", example = "101")
    private Integer displayId;

    @NotNull
    @NotEmpty
    @Schema(description = "First name of the shareholder", example = "John")
    private String firstName;

    @NotNull
    @NotEmpty
    @Schema(description = "Last name of the shareholder", example = "Doe")
    private String lastName;
}
