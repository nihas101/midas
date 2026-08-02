package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.core.accountstatement.dto.LabeledAccountStatement;
import de.nihas101.midas.core.money.MoneyAmount;

public interface RunningTotalAccountStatement extends LabeledAccountStatement {

    MoneyAmount currentBalance();
}
