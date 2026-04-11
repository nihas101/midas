package de.nihas101.midas.openingbalance.entity;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "opening_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpeningBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private ShareholderEntity shareholder;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "amount", nullable = false)
    private MoneyAmount amount = MoneyAmount.ZERO;

    public static OpeningBalanceEntity fromDto(final OpeningBalance openingBalance, final ShareholderEntity shareholderEntity) {
        if (openingBalance == null) {
            return new OpeningBalanceEntity();
        }

        return new OpeningBalanceEntity(
                openingBalance.getId(),
                shareholderEntity,
                openingBalance.getYear().atDay(1),
                openingBalance.getOpeningBalance()
        );
    }
}
