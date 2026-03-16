package de.nihas101.midas.interest.dto;

import de.nihas101.midas.interest.entity.InterestRateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestRate {

    private Integer id;
    private Integer shareholderId;
    private BigDecimal interestRate;
    private Year year;

    public static InterestRate fromEntity(InterestRateEntity interestRateEntity) {
        if (interestRateEntity == null) {
            return null;
        }

        return new InterestRate(
                interestRateEntity.getId(),
                interestRateEntity.getShareholder().getId(),
                new BigDecimal(interestRateEntity.getRate())
                        .setScale(2, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP),
                Year.from(interestRateEntity.getDate())
        );
    }
}
