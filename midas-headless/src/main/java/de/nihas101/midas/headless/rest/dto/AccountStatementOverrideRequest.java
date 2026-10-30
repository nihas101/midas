package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementOverrideRequest {
    private Integer id;
    private Integer shareholderId;
    private Year year;
    private BookingType bookingType;
    private String labelOverride;
    private MoneyAmount amount;
    private boolean hidden;
}
