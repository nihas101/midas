package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.Transaction;
import de.nihas101.midas.core.interest.interestamount.DefaultInterest;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OpeningBalanceInterestCalculationRow implements InterestCalculationRow {

    private final InterestCalculationRow interestCalculationRow;

    public OpeningBalanceInterestCalculationRow(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                bookings,
                year,
                interestRate,
                BaseInterestCalculationRow.DEFAULT_INTEREST_DAYS,
                messageSource,
                locale
        );
    }

    public OpeningBalanceInterestCalculationRow(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate,
            final BigDecimal interestDays,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final BigDecimal effectiveInterestDays = interestDays != null
                ? interestDays
                : BaseInterestCalculationRow.DEFAULT_INTEREST_DAYS;

        this.interestCalculationRow = new BaseInterestCalculationRow(
                messageSource.getMessage(
                        "interest.opening-balance",
                        new Object[]{year.format(DateTimeFormatter.ofPattern("yyyy"))},
                        locale
                ),
                bookings.openingBalance().getOpeningBalance(),
                bookings.openingBalance().getOpeningBalance(),
                effectiveInterestDays,
                new DefaultInterest(
                        bookings.openingBalance().getOpeningBalance(),
                        effectiveInterestDays,
                        interestRate
                ).interestAmount()
        );
    }

    @Override
    public String label() {
        return interestCalculationRow.label();
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
    public BigDecimal interestDaysCount() {
        return interestCalculationRow.interestDaysCount();
    }

    @Override
    public BigDecimal interestAmount() {
        // TODO: Extract the scaling etc into a wrapper to share the code where necessary
        return interestCalculationRow.interestAmount().setScale(0, RoundingMode.HALF_UP);
    }
}
