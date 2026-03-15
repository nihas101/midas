package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.Bookings;
import lombok.RequiredArgsConstructor;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RequiredArgsConstructor
public class OpeningBalanceInterestCalculationRow implements InterestCalculationRow { // TODO: Test

    private final InterestCalculationRow interestCalculationRow;

    public OpeningBalanceInterestCalculationRow(
            final Bookings bookings,
            final Year year,
            final Locale locale
    ) {
        this(
                new BaseInterestCalculationRow(
                        "Vortrag per 01.01." + year.format(DateTimeFormatter.ofPattern("yyyy")),
                        bookings.openingBalance(),
                        bookings.openingBalance(),
                        0, // TODO: Calculate (or is this always 300)?
                        locale
                )
        );
    }

    @Override
    public String monthAsString() {
        return interestCalculationRow.monthAsString();
    }

    @Override
    public String totalTransactionAmountAsString() {
        return "";
    }

    @Override
    public String balanceAtEndOfMonth() {
        return interestCalculationRow.balanceAtEndOfMonth();
    }

    @Override
    public int interestDaysCount() {
        return interestCalculationRow.interestDaysCount();
    }

    @Override
    public int interestAmount() {
        return interestCalculationRow.interestAmount();
    }
}
