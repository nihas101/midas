package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.commons.MoneyAmount;

public interface RunningTotalAccountStatement extends LabeledAccountStatement {

    MoneyAmount currentBalance();
}
