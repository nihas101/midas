package de.nihas101.midas.api.interest;

import de.nihas101.midas.api.bookings.Bookings;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Locale;

public interface InterestRowService {
    List<InterestCalculationRow> generateRows(
            Year year,
            Bookings bookings,
            BigDecimal interestRate,
            InterestCalculation interestCalculation,
            Locale locale
    );
}
