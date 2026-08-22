package de.nihas101.midas.core.interest;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.MonthlyTotalSum;
import de.nihas101.midas.api.interest.Interest;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyCumulativeSum;
import de.nihas101.midas.core.interest.interestamount.DefaultInterest;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class DefaultInterestCalculation implements InterestCalculation {
    private final BigDecimal interestSum;
    private final BigDecimal divisor;
    private final MoneyAmount interest;
    private final MoneyAmount finalSum;
    private final Map<Month, MonthlyTotalSum> monthlyTotalSums;
    private final Map<Month, MoneyAmount> monthlyBalances;
    private final Map<Month, Interest> interests;

    public DefaultInterestCalculation(
            final Bookings bookings,
            final Year year,
            final BigDecimal interestRate
    ) {
        final Map<Month, MoneyAmount> monthlyBookingSums = monthlyBookingSums(bookings);
        final MoneyAmount openingBalance = openingBalance(bookings);
        final Map<Month, MoneyAmount> monthlyBalances = monthlyBalances(openingBalance, monthlyBookingSums);
        final Map<Month, Interest> interests = interests(interestRate, monthlyBalances);
        final BigDecimal interestSum = interestSum(interestRate, openingBalance, interests);
        final BigDecimal divisor = divisor(interestRate, interests);
        final MoneyAmount interest = interest(divisor, interestSum);

        this.interestSum = interestSum;
        this.divisor = divisor;
        this.interest = interest;
        this.finalSum = monthlyBalances.get(Month.DECEMBER).plus(interest);
        this.monthlyTotalSums = monthlyTotalSums(bookings, year);
        this.monthlyBalances = monthlyBalances;
        this.interests = interests;
    }

    private Map<Month, MonthlyTotalSum> monthlyTotalSums(final Bookings bookings, final Year year) {
        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> new DefaultMonthlyTotalSum(bookings, year.atMonth(month).getMonth())));
    }

    private MoneyAmount interest(final BigDecimal divisor, final BigDecimal interestSum) {
        return divisor.compareTo(ZERO) > 0
                ? MoneyAmount.of(interestSum.setScale(4, RoundingMode.HALF_UP).divide(divisor, RoundingMode.HALF_UP))
                : MoneyAmount.ZERO;
    }

    private Map<Month, MoneyAmount> monthlyBookingSums(final Bookings bookings) {
        final Map<Month, MonthlyCumulativeSum> monthlyCumulativeSums = Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> new MonthlyCumulativeSum(bookings, month)));

        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> monthlyCumulativeSums.get(month).sum()));
    }

    private static MoneyAmount openingBalance(final Bookings bookings) {
        return bookings.openingBalance() != null
                ? bookings.openingBalance().getOpeningBalance()
                : MoneyAmount.ZERO;
    }

    private BigDecimal divisor(final BigDecimal interestRate, final Map<Month, Interest> interests) {
        final BigDecimal daysInInterestYear = interests.values()
                .stream()
                .map(Interest::interestDays)
                .reduce(ZERO, BigDecimal::add);

        return (interestRate != null && interestRate.compareTo(ZERO) > 0)
                ? daysInInterestYear.divide(interestRate, RoundingMode.HALF_UP)
                : ZERO;
    }

    private BigDecimal interestSum(final BigDecimal interestRate, final MoneyAmount openingBalance, final Map<Month, Interest> interests) {
        final BigDecimal openingInterest = new DefaultInterest(
                openingBalance,
                valueOf(30L),
                interestRate
        ).interestAmount();

        return interests.entrySet()
                .stream()
                .filter(e -> !Month.DECEMBER.equals(e.getKey()))
                .map(Map.Entry::getValue)
                .map(Interest::interestAmount)
                .reduce(ZERO, BigDecimal::add)
                .add(openingInterest);
    }

    private Map<Month, MoneyAmount> monthlyBalances(final MoneyAmount openingBalance, final Map<Month, MoneyAmount> monthlyBookingSums) {
        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> openingBalance.plus(monthlyBookingSums.get(month))));
    }

    private Map<Month, Interest> interests(final BigDecimal interestRate, final Map<Month, MoneyAmount> monthlyBalances) {
        return Arrays.stream(Month.values())
                .collect(Collectors.toMap(Function.identity(), month -> new DefaultInterest(
                        monthlyBalances.get(month),
                        valueOf(30L),
                        interestRate
                )));
    }

    @Override
    public BigDecimal interestSum() {
        return interestSum;
    }

    @Override
    public BigDecimal divisor() {
        return divisor;
    }

    @Override
    public MoneyAmount interest() {
        return interest;
    }

    @Override
    public MoneyAmount finalSum() {
        return finalSum;
    }

    @Override
    public Map<Month, MonthlyTotalSum> monthlyTotalSums() {
        return monthlyTotalSums;
    }

    @Override
    public Map<Month, MoneyAmount> monthlyBalances() {
        return monthlyBalances;
    }

    @Override
    public Map<Month, Interest> interests() {
        return interests;
    }

}
