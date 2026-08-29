package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.Transaction;
import de.nihas101.midas.commons.MoneyAmount;
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

        final MoneyAmount openingBalance = bookings.openingBalance().getOpeningBalance();
        this.interestCalculationRow = new BaseInterestCalculationRow(
                messageSource.getMessage(
                        "interest.opening-balance",
                        new Object[]{year.format(DateTimeFormatter.ofPattern("yyyy"))},
                        locale
                ),
                effectiveInterestDays,
                new DefaultInterest(
                        openingBalance,
                        effectiveInterestDays,
                        interestRate
                ).interestAmount(),
                new Transaction(openingBalance),
                new Transaction(openingBalance)
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
        return interestCalculationRow.interestAmount().setScale(0, RoundingMode.HALF_UP);
    }
}
