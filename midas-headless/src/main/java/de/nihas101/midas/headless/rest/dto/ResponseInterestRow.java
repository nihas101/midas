package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.commons.MoneyAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Calculated monthly row for interest breakdown")
public class ResponseInterestRow {

    @Schema(description = "Period label or month name", example = "January")
    private String label;

    @Schema(description = "Account balance at the end of the calculation period")
    private MoneyAmountDto balanceAtEndOfMonth;

    @Schema(description = "Calculated interest amount for the period", example = "12.34")
    private BigDecimal interestAmount;

    @Schema(description = "Number of interest days in the period", example = "30")
    private BigDecimal interestDaysCount;

    @Schema(description = "Sum of transactions during the period")
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
