package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.CumulativeSumMonthlyTotalCalculator;
import de.nihas101.midas.bookings.monthlytotal.MonthlySumTotalCalculator;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalCalculator;
import lombok.RequiredArgsConstructor;

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
            final Locale locale
    ) {
        this(
                bookings,
                new MonthlySumTotalCalculator(bookings, yearMonth.getMonth()),
                new CumulativeSumMonthlyTotalCalculator(bookings, yearMonth.getMonth()),
                yearMonth,
                locale
        );
    }

    public DefaultInterestCalculationRow(
            final Bookings bookings,
            final MonthlySumTotalCalculator monthlyTotalCalculator,
            final CumulativeSumMonthlyTotalCalculator cumulativeSumMonthlyTotalCalculator,
            final YearMonth yearMonth,
            final Locale locale
    ) {
        final Map<BookingType, MoneyAmount> monthTotals = monthlyTotalCalculator.monthlyTotal();
        final MoneyAmount monthTotalSum = monthTotals.values()
                .stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);

        final Map<BookingType, MoneyAmount> cumulativeTotals = cumulativeSumMonthlyTotalCalculator.monthlyTotal();
        final MoneyAmount totalSumOfAllBookings = cumulativeTotals.values().stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
        final MoneyAmount balanceAtEndOfMonth = bookings.initialBalance().plus(totalSumOfAllBookings);

        this.interestCalculationRow = new BaseInterestCalculationRow(
                yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("dd. MMMM", locale)),
                monthTotalSum,
                balanceAtEndOfMonth,
                0, // TODO: Calculate
                locale
        );
    }

    @Override
    public String monthAsString() {
        return interestCalculationRow.monthAsString();
    }

    @Override
    public String totalTransactionAmountAsString() {
        return interestCalculationRow.totalTransactionAmountAsString();
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
