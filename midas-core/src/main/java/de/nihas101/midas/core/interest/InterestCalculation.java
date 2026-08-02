package de.nihas101.midas.core.interest;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyCumulativeSum;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.core.interest.interestamount.Interest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

// TODO: Tests
// TODO: These constructors are a mess, extract classes from that logic
public record InterestCalculation(
        BigDecimal interestSum,
        BigDecimal divisor,
        MoneyAmount interest,
        MoneyAmount finalSum,
        Map<Month, MonthlyTotalSum> monthlyTotalSums,
        Map<Month, MoneyAmount> monthlyBalances,
        Map<Month, Interest> interests
) {

    public InterestCalculation(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate
    ) {
        this(
                bookings,
                year,
                interestRate,
                getMonthlyBalances(bookings, getMonthlyBookingSums(getMonthlyCumulativeSums(bookings)))
        );
    }

    public InterestCalculation(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate,
            final Map<Month, MoneyAmount> monthlyBalances
    ) {
        this(
                bookings,
                year,
                interestRate,
                monthlyBalances,
                Arrays.stream(Month.values()).collect(
                        Collectors.toMap(Function.identity(), month -> new Interest(
                                monthlyBalances.get(month),
                                valueOf(30L),
                                interestRate
                        )))
        );
    }

    private static Map<Month, MoneyAmount> getMonthlyBalances(final Bookings bookings, final Map<Month, MoneyAmount> monthlyBookingSums) {
        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> bookings.openingBalance()
                        .getOpeningBalance()
                        .plus(monthlyBookingSums.get(month))));
    }

    private static Map<Month, MoneyAmount> getMonthlyBookingSums(final Map<Month, MonthlyCumulativeSum> monthlyCumulativeSums) {
        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> {
                    final MonthlyCumulativeSum monthlyCumulativeSum = monthlyCumulativeSums.get(month);
                    return monthlyCumulativeSum.sum();
                }));
    }

    private static Map<Month, MonthlyCumulativeSum> getMonthlyCumulativeSums(final Bookings bookings) {
        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> new MonthlyCumulativeSum(bookings, month)));
    }

    public InterestCalculation(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate,
            final Map<Month, MoneyAmount> monthlyBalances,
            final Map<Month, Interest> interests
    ) {
        this(
                interests.entrySet()
                        .stream()
                        // The last month is excluded from the sum of interests
                        .filter(e -> !Month.DECEMBER.equals(e.getKey()))
                        .map(Map.Entry::getValue)
                        .map(Interest::interestAmount)
                        .reduce(ZERO, BigDecimal::add)
                        .add(
                                new Interest(
                                        bookings.openingBalance() != null ? bookings.openingBalance().getOpeningBalance() : MoneyAmount.ZERO,
                                        valueOf(30L),
                                        interestRate
                                ).interestAmount()
                        ),
                interests.values()
                        .stream()
                        .map(Interest::interestDays)
                        .reduce(ZERO, BigDecimal::add),
                interestRate,
                Arrays.stream(Month.values())
                        .collect(Collectors.toMap(Function.identity(), month -> new MonthlyTotalSum(bookings, year.atMonth(month).getMonth()))),
                monthlyBalances,
                interests
        );
    }

    public InterestCalculation(
            final BigDecimal interestSum,
            final BigDecimal daysInInterestYear,
            final BigDecimal interestRate,
            final Map<Month, MonthlyTotalSum> monthlyTotalSums,
            final Map<Month, MoneyAmount> monthlyBalances,
            final Map<Month, Interest> interests
    ) {
        this(
                interestSum,
                interestRate.compareTo(ZERO) > 0 ? daysInInterestYear.divide(interestRate, RoundingMode.HALF_UP) : ZERO, // divisor
                monthlyTotalSums,
                monthlyBalances,
                interests
        );
    }

    public InterestCalculation(
            final BigDecimal interestSum,
            final BigDecimal divisor,
            final Map<Month, MonthlyTotalSum> monthlyTotalSums,
            final Map<Month, MoneyAmount> monthlyBalances,
            final Map<Month, Interest> interests
    ) {
        this(
                interestSum,
                divisor,
                // interest
                divisor.compareTo(ZERO) > 0
                        ? MoneyAmount.of(interestSum.setScale(4, RoundingMode.HALF_UP).divide(divisor, RoundingMode.HALF_UP))
                        : MoneyAmount.ZERO,
                monthlyTotalSums,
                monthlyBalances,
                interests
        );
    }

    public InterestCalculation(
            BigDecimal interestSum,
            BigDecimal divisor,
            MoneyAmount interest,
            Map<Month, MonthlyTotalSum> monthlyTotalSums,
            Map<Month, MoneyAmount> monthlyBalances,
            Map<Month, Interest> interests
    ) {
        this(
                interestSum,
                divisor,
                interest,
                monthlyBalances.get(Month.DECEMBER).plus(interest), // finalSum
                monthlyTotalSums,
                monthlyBalances,
                interests
        );
    }
}
