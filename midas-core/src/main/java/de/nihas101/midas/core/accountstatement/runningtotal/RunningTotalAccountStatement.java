package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.money.MoneyAmount;

public interface RunningTotalAccountStatement extends LabeledAccountStatement {

    MoneyAmount currentBalance();
}
