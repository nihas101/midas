package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.LabeledAccountStatement;
import de.nihas101.midas.money.MoneyAmount;

public interface RunningTotalAccountStatement extends LabeledAccountStatement {

    MoneyAmount currentBalance();
}
