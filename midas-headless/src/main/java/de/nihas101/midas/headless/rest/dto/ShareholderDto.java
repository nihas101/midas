package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
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
public class ShareholderDto {
    private Integer id; // TODO: Not needed in req

    @NotNull
    @Positive
    private Integer displayId;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    public static ShareholderDto toDto(final Shareholder shareholder) {
        return ShareholderDto.builder()
                .id(shareholder.getId())
                .displayId(shareholder.getDisplayId())
                .firstName(shareholder.getFirstName())
                .lastName(shareholder.getLastName())
                .build();
    }
}
