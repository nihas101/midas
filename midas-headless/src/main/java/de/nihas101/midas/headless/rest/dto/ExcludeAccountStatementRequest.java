package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
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
public class ExcludeAccountStatementRequest {
    @NotNull
    private Integer shareholderId;
    @NotNull
    private Year year;
    @NotNull
    private BookingType bookingType;
    private boolean exclude;
}
