package de.nihas101.midas.core.accountstatement.runningtotal;

import java.util.List;

public interface RunningTotalAccountStatements {
    List<RunningTotalAccountStatement> runningTotalAccountStatements();

    boolean isEmpty();
}
