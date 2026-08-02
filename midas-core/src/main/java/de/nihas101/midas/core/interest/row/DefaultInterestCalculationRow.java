package de.nihas101.midas.core.interest.row;

import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.core.interest.interestamount.Interest;
import de.nihas101.midas.core.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
public class DefaultInterestCalculationRow implements InterestCalculationRow {

    private final InterestCalculationRow interestCalculationRow;
    private final String partName;

    public DefaultInterestCalculationRow(
            final YearMonth yearMonth,
            final Interest interest,
            final Locale locale,
            final MoneyAmount balanceAtEndOfMonth,
            final MonthlyTotalSum monthTotalSum,
            final String partName
    ) {
        this(
                new BaseInterestCalculationRow(
                        yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("dd. MMMM", locale)),
                        monthTotalSum.sum(),
                        balanceAtEndOfMonth,
                        Month.DECEMBER == yearMonth.getMonth() ? ZERO : interest.interestAmount()
                ),
                partName
        );
    }

    @Override
    public String label() {
        return interestCalculationRow.label();
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
    public Integer interestDaysCount() {
        return interestCalculationRow.interestDaysCount();
    }

    @Override
    public BigDecimal interestAmount() {
        return interestCalculationRow.interestAmount().setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public String partName() {
        return partName;
    }
}
