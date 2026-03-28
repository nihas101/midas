package de.nihas101.midas.accountstatement.service;

import de.nihas101.midas.accountstatement.dto.AccountStatements;
import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementsRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountStatementService {

    private final AccountStatementsRepository accountStatementsRepository;

    public AccountStatements accountStatement(final Shareholder shareholder, final Year year) {
        final List<AccountStatementEntity> accountStatementEntities = accountStatementsRepository.findSummaryByType(
                shareholder.getId(),
                year.atMonth(Month.JANUARY).atDay(1),
                year.atMonth(Month.DECEMBER).atEndOfMonth()
        );
        return AccountStatements.fromEntity(accountStatementEntities, year);
    }
}
