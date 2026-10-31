package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.Source;
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
public class RequestToggleAutomaticInterestBooking {

    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    private Year year;

    private boolean toggleAutomaticBooking;

    public Source getSource() {
        return toggleAutomaticBooking ? Source.SYSTEM : Source.USER;
    }
}
