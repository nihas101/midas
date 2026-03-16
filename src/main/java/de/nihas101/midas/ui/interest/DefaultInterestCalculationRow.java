package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.CumulativeSumMonthlyTotalCalculator;
import de.nihas101.midas.bookings.monthlytotal.MonthlySumTotalCalculator;
import de.nihas101.midas.interest.interestamount.Interest;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

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
                new MonthlySumTotalCalculator(bookings, yearMonth.getMonth()),
                new CumulativeSumMonthlyTotalCalculator(bookings, yearMonth.getMonth()),
                yearMonth,
                interestRate, locale
        );
    }

    public DefaultInterestCalculationRow(
            final Bookings bookings,
            final MonthlySumTotalCalculator monthlyTotalCalculator,
            final CumulativeSumMonthlyTotalCalculator cumulativeSumMonthlyTotalCalculator,
            final YearMonth yearMonth,
            final BigDecimal interestRate,
            final Locale locale
    ) {
        final Map<BookingType, MoneyAmount> monthTotals = monthlyTotalCalculator.monthlyTotal();
        final MoneyAmount monthTotalSum = monthTotals.values()
                .stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);

        final Map<BookingType, MoneyAmount> cumulativeTotals = cumulativeSumMonthlyTotalCalculator.monthlyTotal();
        final MoneyAmount totalSumOfAllBookings = cumulativeTotals.values().stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
        final MoneyAmount balanceAtEndOfMonth = bookings.openingBalance().plus(totalSumOfAllBookings);

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
