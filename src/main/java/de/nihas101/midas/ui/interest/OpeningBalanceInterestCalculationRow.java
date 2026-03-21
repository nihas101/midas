package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.interest.interestamount.Interest;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class OpeningBalanceInterestCalculationRow implements InterestCalculationRow { // TODO: Test

    private final InterestCalculationRow interestCalculationRow;

    public OpeningBalanceInterestCalculationRow(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate
    ) {
        this(
                new BaseInterestCalculationRow(
                        "Vortrag per 01.01." + year.format(DateTimeFormatter.ofPattern("yyyy")), // TODO: i18n
                        bookings.openingBalance().getOpeningBalance(),
                        bookings.openingBalance().getOpeningBalance(),
                        new Interest(
                                bookings.openingBalance().getOpeningBalance(),
                                BigDecimal.valueOf(30L), // TODO: Make this passable from outside
                                interestRate
                        ).interestAmount()
                )
        );
    }

    @Override
    public String monthAsString() {
        return interestCalculationRow.monthAsString();
    }

    @Override
    public Transaction totalTransaction() {
        return null;
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return interestCalculationRow.balanceAtEndOfMonth();
    }

    @Override
    public Integer interestDaysCount() {
        return interestCalculationRow.interestDaysCount();
    }

    @Override
    public BigDecimal interestAmount() {
        // TODO: Extract the scaling etc into a wrapper to share the code where necessary
        return interestCalculationRow.interestAmount().setScale(0, RoundingMode.HALF_UP);
    }
}
