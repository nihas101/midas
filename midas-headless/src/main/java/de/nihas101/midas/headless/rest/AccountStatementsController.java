package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.accountstatement.service.AccountStatementSort;
import de.nihas101.midas.core.accountstatement.service.DefaultAccountStatementService;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.headless.rest.dto.AccountStatementOrderRequest;
import de.nihas101.midas.headless.rest.dto.AccountStatementOverrideRequest;
import de.nihas101.midas.headless.rest.dto.AccountStatementsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/account-statements")
@RequiredArgsConstructor
public class AccountStatementsController {

    private final DefaultAccountStatementService accountStatementService;
    private final ShareholdersService shareholdersService;
    private final MessageSource messageSource;
    private final ShareholderLock shareholderLock;
    private final AccountStatementSort accountStatementSort;

    @GetMapping
    public ResponseEntity<AccountStatementsResponse> getAccountStatements(
            @RequestParam final Integer shareholderId,
            @RequestParam(name = "year") final Integer yearValue,
            final Locale locale
    ) {
        final Shareholder sh = shareholdersService.shareholder(shareholderId);
        if (sh == null) {
            return ResponseEntity.notFound().build();
        }

        final Locale loc = locale != null ? locale : LocaleContextHolder.getLocale();
        final Year year = Year.of(yearValue);
        final AccountStatements statements = accountStatementService.accountStatements(
                sh,
                year,
                messageSource,
                loc
        );
        List<LabeledAccountStatement> s = new ArrayList<>(statements.accountStatements());
        s.addAll(statements.manualStatements());
        accountStatementSort.sort(s, sh, year);

        return ResponseEntity.ok(AccountStatementsResponse.builder()
                .accountStatements(s)
                .build());
    }

    @PostMapping("/overrides")
    public ResponseEntity<Void> saveOverride(@RequestBody final AccountStatementOverrideRequest request) {
        shareholderLock.assertUnlocked(request.getShareholderId(), request.getYear());

        final Shareholder sh = shareholdersService.shareholder(request.getShareholderId());
        if (sh == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getBookingType() != null) {
            accountStatementService.saveOverride(
                    sh,
                    request.getYear(),
                    request.getBookingType(),
                    request.getAmount(),
                    request.isHidden()
            );
        } else {
            accountStatementService.saveManualExtra(
                    request.getId(),
                    sh,
                    request.getYear(),
                    request.getLabelOverride(),
                    request.getAmount()
            );
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/overrides/{id}")
    public ResponseEntity<Void> deleteOverride(
            @PathVariable final Integer id,
            @RequestParam final Integer shareholderId,
            @RequestParam final Integer year
    ) {
        shareholderLock.assertUnlocked(shareholderId, Year.of(year));
        accountStatementService.deleteOverride(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/order")
    public ResponseEntity<Void> saveOrder(@RequestBody AccountStatementOrderRequest request) {
        shareholderLock.assertUnlocked(request.getShareholderId(), request.getYear());

        final Shareholder sh = shareholdersService.shareholder(request.getShareholderId());
        if (sh == null) {
            return ResponseEntity.notFound().build();
        }

        accountStatementService.saveOrder(sh, request.getYear(), request.getRowKeys());
        return ResponseEntity.ok().build();
    }
}
