package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInterestRow {

    // TODO: Documentation
    private String label;
    private MoneyAmountDto balanceAtEndOfMonth;
    private BigDecimal interestAmount;
    private BigDecimal interestDaysCount;
    private MoneyAmountDto totalTransaction;

    public static ResponseInterestRow toDto(final InterestCalculationRow interestCalculationRow) {
        if (interestCalculationRow == null) {
            return null;
        }

        return new ResponseInterestRow(
                interestCalculationRow.label(),
                new MoneyAmountDto(
                        interestCalculationRow.balanceAtEndOfMonth() != null
                                ? interestCalculationRow.balanceAtEndOfMonth().moneyAmount()
                                : MoneyAmount.ZERO
                ),
                interestCalculationRow.interestAmount(),
                interestCalculationRow.interestDaysCount(),
                new MoneyAmountDto(
                        interestCalculationRow.totalTransaction() != null
                                ? interestCalculationRow.totalTransaction().moneyAmount()
                                : MoneyAmount.ZERO
                )
        );
    }
}
