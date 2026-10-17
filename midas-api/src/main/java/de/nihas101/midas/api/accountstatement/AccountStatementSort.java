package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.api.shareholder.Shareholder;

import java.time.Year;
import java.util.List;

public interface AccountStatementSort {
    List<LabeledAccountStatement> sort(
            List<LabeledAccountStatement> allStatements,
            Shareholder shareholder,
            Year year
    );
}
