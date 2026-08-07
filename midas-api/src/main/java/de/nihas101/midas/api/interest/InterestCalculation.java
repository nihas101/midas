package de.nihas101.midas.api.interest;

import de.nihas101.midas.api.bookings.MonthlyTotalSum;
import de.nihas101.midas.commons.MoneyAmount;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;

public interface InterestCalculation {
    BigDecimal interestSum();

    BigDecimal divisor();

    MoneyAmount interest();

    MoneyAmount finalSum();

    Map<Month, MonthlyTotalSum> monthlyTotalSums();

    Map<Month, MoneyAmount> monthlyBalances();

    Map<Month, Interest> interests();
}
