package de.nihas101.midas.api.accountstatement;

import java.util.List;

public interface RunningTotalAccountStatements {
    List<RunningTotalAccountStatement> runningTotalAccountStatements();

    boolean isEmpty();
}
