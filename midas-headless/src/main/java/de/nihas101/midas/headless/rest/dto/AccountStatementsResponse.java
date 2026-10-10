package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.accountstatement.AccountStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementsResponse {
    private List<? extends AccountStatement> accountStatements;
}
