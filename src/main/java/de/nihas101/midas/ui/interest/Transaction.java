package de.nihas101.midas.ui.interest;

import de.nihas101.midas.money.MoneyAmount;

public record Transaction(MoneyAmount moneyAmount) {

    @Override
    public MoneyAmount moneyAmount() {
        return moneyAmount.abs();
    }

    public TransactionType type() {
        return moneyAmount.smallerThan(MoneyAmount.ZERO) ? TransactionType.DEBIT : TransactionType.CREDIT;
    }
}
