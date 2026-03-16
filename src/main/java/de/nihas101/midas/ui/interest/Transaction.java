package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Transaction {

    private final MoneyAmount moneyAmount;

    public MoneyAmount moneyAmount() {
        return moneyAmount.abs();
    }

    public TransactionType type() {
        return moneyAmount.smallerThan(MoneyAmount.ZERO) ? TransactionType.DEBIT : TransactionType.CREDIT;
    }
}
