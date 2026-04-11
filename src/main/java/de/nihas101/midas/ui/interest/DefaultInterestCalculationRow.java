package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.interest.interestamount.Interest;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
                        interest.interestAmount()
                ),
                partName
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
