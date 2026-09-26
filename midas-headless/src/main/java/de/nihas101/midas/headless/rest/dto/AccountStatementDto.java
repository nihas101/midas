package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto {
    private Integer id;
    private BookingType type;
    private LocalDate date;
    private MoneyAmount amount;
    private String label;
    private boolean hidden;
    private boolean manualExtra;
    private String rowKey;
}
