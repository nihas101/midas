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

    private static final BigDecimal MULTIPLIER = BigDecimal.valueOf(100);

    private Integer id;
    private Integer shareholderId;
    private BigDecimal interestRate;
    private Year year;

    public static InterestRate fromEntity(final InterestRateEntity interestRateEntity) {
        if (interestRateEntity == null) {
            return null;
        }

        return new InterestRate(
                interestRateEntity.getId(),
                interestRateEntity.getShareholder().getId(),
                scaleDownFromDatabase(interestRateEntity),
                Year.from(interestRateEntity.getDate())
        );
    }

    private static BigDecimal scaleDownFromDatabase(final InterestRateEntity interestRateEntity) {
        return new BigDecimal(interestRateEntity.getRate())
                .setScale(2, RoundingMode.HALF_UP)
                .divide(MULTIPLIER, RoundingMode.HALF_UP);
    }

    public Long toEntity() {
        return interestRate.multiply(MULTIPLIER)
                .longValue();
    }
}
