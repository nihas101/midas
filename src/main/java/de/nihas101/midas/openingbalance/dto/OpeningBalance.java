package de.nihas101.midas.openingbalance.dto;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.entity.OpeningBalanceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpeningBalance {

    private Integer id;
    private Integer shareholderId;
    private MoneyAmount openingBalance;
    private Year year;

    public static OpeningBalance fromEntity(OpeningBalanceEntity openingBalanceEntity) {
        if (openingBalanceEntity == null) {
            return null;
        }

        return new OpeningBalance(
                openingBalanceEntity.getId(),
                openingBalanceEntity.getShareholder().getId(),
                openingBalanceEntity.getAmount(),
                Year.from(openingBalanceEntity.getDate())
        );
    }
}
