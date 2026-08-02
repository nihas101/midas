package de.nihas101.midas.core.openingbalance.dto;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceEntity;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultOpeningBalance implements OpeningBalance {

    private Integer id;
    private Integer shareholderId;
    private MoneyAmount openingBalance;
    private Year year;
    private Source source;

    public DefaultOpeningBalance(final MoneyAmount moneyAmount) {
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

        return new DefaultOpeningBalance(
                openingBalanceEntity.getId(),
                openingBalanceEntity.getShareholder().getId(),
                openingBalanceEntity.getAmount(),
                Year.from(openingBalanceEntity.getDate()),
                openingBalanceEntity.getSource() != null ? openingBalanceEntity.getSource() : Source.USER
        );
    }

    public static OpeningBalanceEntity fromDto(
            final OpeningBalance openingBalance,
            final ShareholderEntity shareholderEntity
    ) {
        if (openingBalance == null) {
            return new OpeningBalanceEntity();
        }

        return new OpeningBalanceEntity(
                openingBalance.getId(),
                shareholderEntity,
                openingBalance.getYear().atDay(1),
                openingBalance.getOpeningBalance(),
                openingBalance.getSource() != null ? openingBalance.getSource() : Source.USER
        );
    }
}
