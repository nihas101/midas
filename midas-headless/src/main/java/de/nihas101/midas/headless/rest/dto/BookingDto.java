package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Integer id;
    private Integer displayId;
    private Integer shareholderId;
    private LocalDate date;
    private BookingType type;
    private MoneyAmount amount;
    private String comment;
    private Source source;
}
