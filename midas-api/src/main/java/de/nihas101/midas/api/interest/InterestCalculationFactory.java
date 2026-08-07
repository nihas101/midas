package de.nihas101.midas.api.interest;

import de.nihas101.midas.api.bookings.Bookings;

import java.math.BigDecimal;
import java.time.Year;

public interface InterestCalculationFactory {
    InterestCalculation create(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate
    );
}
