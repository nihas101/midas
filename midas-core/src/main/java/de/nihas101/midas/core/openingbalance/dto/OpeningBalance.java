package de.nihas101.midas.core.openingbalance.dto;

import de.nihas101.midas.core.bookings.entity.Source;
import de.nihas101.midas.core.money.MoneyAmount;
import de.nihas101.midas.core.openingbalance.entity.OpeningBalanceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data // TODO: Replace with interface?
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpeningBalance {

    private Integer id;
    private Integer shareholderId;
    private MoneyAmount openingBalance;
    private Year year;
    private Source source;

    public OpeningBalance(final MoneyAmount moneyAmount) {
        this(
                null,
                null,
                moneyAmount != null ? moneyAmount : MoneyAmount.ZERO,
                Year.now(),
                Source.USER
        );
    }

    public static OpeningBalance fromEntity(final OpeningBalanceEntity openingBalanceEntity) {
        if (openingBalanceEntity == null) {
            return null;
        }

        return new OpeningBalance(
                openingBalanceEntity.getId(),
                openingBalanceEntity.getShareholder().getId(),
                openingBalanceEntity.getAmount(),
                Year.from(openingBalanceEntity.getDate()),
                openingBalanceEntity.getSource() != null ? openingBalanceEntity.getSource() : Source.USER
        );
    }
}
