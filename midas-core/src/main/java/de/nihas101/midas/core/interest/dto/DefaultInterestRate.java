package de.nihas101.midas.core.interest.dto;

import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.persistance.interest.InterestRateEntity;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

import static java.math.BigDecimal.valueOf;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultInterestRate implements InterestRate {

    private static final BigDecimal MULTIPLIER = valueOf(100);

    private Integer id;
    private Integer shareholderId;
    private BigDecimal interestRate;
    private Year year;

    public static InterestRate fromEntity(final InterestRateEntity interestRateEntity) {
        if (interestRateEntity == null) {
            return null;
        }

        return new DefaultInterestRate(
                interestRateEntity.getId(),
                interestRateEntity.getShareholder().getId(),
                scaleDownFromDatabase(interestRateEntity),
                Year.from(interestRateEntity.getDate())
        );
    }

    public static InterestRateEntity fromDto(final InterestRate interestRate, final ShareholderEntity shareholder) {
        return new InterestRateEntity(
                interestRate.getId(),
                shareholder,
                interestRate.getYear().atDay(1),
                toEntity(interestRate)
        );
    }

    private static BigDecimal scaleDownFromDatabase(final InterestRateEntity interestRateEntity) {
        return new BigDecimal(interestRateEntity.getRate())
                .setScale(2, RoundingMode.HALF_UP)
                .divide(MULTIPLIER, RoundingMode.HALF_UP);
    }

    private static Long toEntity(final InterestRate interestRate) {
        return interestRate.getInterestRate().multiply(MULTIPLIER)
                .longValue();
    }
}
