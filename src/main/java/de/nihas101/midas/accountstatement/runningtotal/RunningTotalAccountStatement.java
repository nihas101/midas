package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatement;
import de.nihas101.midas.money.MoneyAmount;

public interface RunningTotalAccountStatement extends AccountStatement {

    MoneyAmount currentBalance();
}
