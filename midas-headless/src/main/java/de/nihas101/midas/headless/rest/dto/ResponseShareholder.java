package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
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
public class ResponseShareholder {

    @NotNull
    @Positive
    @Schema(description = "Internal unique ID of the shareholder", example = "1")
    private Integer id;

    @NotNull
    @Positive
    @Schema(description = "Display ID of the shareholder", example = "101")
    private Integer displayId;

    @NotNull
    @NotEmpty
    @Schema(description = "First name of the shareholder", example = "John")
    private String firstName;

    @NotNull
    @NotEmpty
    @Schema(description = "Last name of the shareholder", example = "Doe")
    private String lastName;

    public static ResponseShareholder toDto(final Shareholder shareholder) {
        if (shareholder == null) {
            return null;
        }
        return ResponseShareholder.builder()
                .id(shareholder.getId())
                .displayId(shareholder.getDisplayId())
                .firstName(shareholder.getFirstName())
                .lastName(shareholder.getLastName())
                .build();
    }
}
