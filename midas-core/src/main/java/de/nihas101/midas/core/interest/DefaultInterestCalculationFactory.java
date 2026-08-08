package de.nihas101.midas.core.interest;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.interest.InterestCalculationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Year;

@Component
@RequiredArgsConstructor
public class DefaultInterestCalculationFactory implements InterestCalculationFactory {

    @Override
    public InterestCalculation create(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate
    ) {
        return new DefaultInterestCalculation(
                bookings,
                year,
                interestRate
        );
    }
}
