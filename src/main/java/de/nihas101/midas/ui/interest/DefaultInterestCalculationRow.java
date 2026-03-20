package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.monthlytotal.MonthlyCumulativeSum;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.interest.interestamount.Interest;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RequiredArgsConstructor
public class DefaultInterestCalculationRow implements InterestCalculationRow {

    private final InterestCalculationRow interestCalculationRow;

    public DefaultInterestCalculationRow(
            final Bookings bookings,
            final YearMonth yearMonth,
            final BigDecimal interestRate,
            final Locale locale
    ) {
        this(
                bookings,
                new MonthlyTotalSum(bookings, yearMonth.getMonth()),
                new MonthlyCumulativeSum(bookings, yearMonth.getMonth()),
                yearMonth,
                interestRate, locale
        );
    }

    public DefaultInterestCalculationRow(
            final Bookings bookings,
            final MonthlyTotalSum monthlyTotalSum,
            final MonthlyCumulativeSum monthlyCumulativeSum,
            final YearMonth yearMonth,
            final BigDecimal interestRate,
            final Locale locale
    ) {
        final MoneyAmount monthTotalSum = monthlyTotalSum.sum();

        final MoneyAmount totalSumOfAllBookings = monthlyCumulativeSum.sum();
        final MoneyAmount balanceAtEndOfMonth = bookings.openingBalance()
                .getOpeningBalance()
                .plus(totalSumOfAllBookings);

        this.interestCalculationRow = new BaseInterestCalculationRow(
                yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("dd. MMMM", locale)),
                monthTotalSum,
                balanceAtEndOfMonth,
                new Interest(
                        balanceAtEndOfMonth,
                        BigDecimal.valueOf(30L),
                        interestRate
                ).interestAmount(),
                locale
        );
    }

    @Override
    public String monthAsString() {
        return interestCalculationRow.monthAsString();
    }

    @Override
    public Transaction totalTransaction() {
        return interestCalculationRow.totalTransaction();
    }

    @Override
    public Transaction balanceAtEndOfMonth() {
        return interestCalculationRow.balanceAtEndOfMonth();
    }

    @Override
    public int interestDaysCount() {
        return interestCalculationRow.interestDaysCount();
    }

    @Override
    public BigDecimal interestAmount() {
        return interestCalculationRow.interestAmount();
    }
}
