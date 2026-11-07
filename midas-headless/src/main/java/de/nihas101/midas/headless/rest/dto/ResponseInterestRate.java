package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.interest.InterestRate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInterestRate {
    private Integer id;

    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    @Positive
    private BigDecimal interestRate;

    @NotNull
    private Year year;

    public static ResponseInterestRate toDto(final InterestRate rate) {
        return ResponseInterestRate.builder()
                .id(rate.getId())
                .shareholderId(rate.getShareholderId())
                .interestRate(rate.getInterestRate())
                .year(rate.getYear())
                .build();
    }
}
