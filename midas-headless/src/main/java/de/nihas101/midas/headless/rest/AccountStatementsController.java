package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.accountstatement.AccountStatementService;
import de.nihas101.midas.api.accountstatement.AccountStatementSort;
import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.headless.rest.dto.AccountStatementDto;
import de.nihas101.midas.headless.rest.dto.AccountStatementOrderRequest;
import de.nihas101.midas.headless.rest.dto.AccountStatementsResponse;
import de.nihas101.midas.headless.rest.dto.CreateManualExtraRequest;
import de.nihas101.midas.headless.rest.dto.ExcludeAccountStatementRequest;
import de.nihas101.midas.headless.rest.dto.ManualStatementDto;
import de.nihas101.midas.headless.rest.dto.MoneyAmountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@Tag(name = "Account Statements", description = "Controller for account statements CRUD")
public class AccountStatementsController {

    private final AccountStatementService accountStatementService;
    private final ShareholdersReader shareholdersReader;
    private final MessageSource messageSource;
    private final AccountStatementSort accountStatementSort;

    @Operation(
            summary = "Get the account statements for the shareholder and year",
            description = "Returns the account statements for the specified shareholder and year."
    )
    @GetMapping
    public ResponseEntity<AccountStatementsResponse> getAccountStatements(
            @RequestParam
            @NotNull
            @Positive final Integer shareholderId,

            @RequestParam(name = "year")
            @NotNull
            @Positive final Integer yearValue,

            final Locale locale
    ) {
        final Shareholder shareholder = shareholdersReader.shareholder(shareholderId);
        if (shareholder == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        final Locale loc = locale != null ? locale : LocaleContextHolder.getLocale();
        final Year year = Year.of(yearValue);
        final AccountStatements statements = accountStatementService.accountStatements(
                shareholder,
                year,
                messageSource,
                loc
        );
        final List<LabeledAccountStatement> s = new ArrayList<>(statements.accountStatements());
        s.addAll(statements.manualStatements());
        final List<LabeledAccountStatement> sorted = accountStatementSort.sort(s, shareholder, year);

        final List<AccountStatementDto> stmtDtos = sorted.stream()
                .map(item -> AccountStatementDto.builder()
                        .id(item.id())
                        .type(item.type())
                        .date(item.date())
                        .amount(MoneyAmountDto.fromDomain(item.amount()))
                        .label(item.label())
                        .hidden(item.isHidden())
                        .manualExtra(item.isManualExtra())
                        .rowKey(item.rowKey())
                        .build())
                .toList();

        final List<ManualStatementDto> overrideDtos = statements.manualStatements().stream()
                .map(m -> ManualStatementDto.builder()
                        .id(m.id())
                        .labelOverride(m.label())
                        .amount(MoneyAmountDto.fromDomain(m.amount()))
                        .build())
                .toList();

        return ResponseEntity.ok(
                AccountStatementsResponse.builder()
                        .accountStatements(stmtDtos)
                        .overrides(overrideDtos)
                        .build()
        );
    }

    @Operation(
            summary = "Update the order of account statements for a shareholder and year",
            description = "Updates the order of account statements for the specified shareholder and year."
    )
    @PutMapping("/order")
    public ResponseEntity<Void> updateOrder(
            @NotNull
            @RequestBody final AccountStatementOrderRequest orderRequest
    ) {
        accountStatementService.saveOrder(
                shareholdersReader.shareholder(orderRequest.getShareholderId()),
                orderRequest.getYear(),
                orderRequest.getRowKeys()
        );
        return ResponseEntity.ok()
                .build();
    }

    @Operation(
            summary = "Create a manual statement for a shareholder and year",
            description = "Creates a manual statement for the specified shareholder and year."
    )
    @PostMapping("/manual-extra")
    public ResponseEntity<Void> createManualExtra(
            @NotNull
            @RequestBody final CreateManualExtraRequest request
    ) {
        final Shareholder shareholder = shareholdersReader.shareholder(request.getShareholderId());
        if (shareholder == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        accountStatementService.saveManualExtra(
                null,
                shareholder,
                request.getYear(),
                request.getLabelOverride(),
                request.getAmount() != null ? request.getAmount().toDomain() : null
        );
        return ResponseEntity.ok()
                .build();
    }

    @Operation(
            summary = "Set whether an account statement is excluded for a shareholder and year",
            description = "Sets whether an account statement is excluded for the specified shareholder and year."
    )
    @PostMapping("/exclude")
    public ResponseEntity<Void> excludeStatement(
            @NotNull
            @RequestBody final ExcludeAccountStatementRequest request
    ) {
        final Shareholder shareholder = shareholdersReader.shareholder(request.getShareholderId());
        if (shareholder == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        accountStatementService.saveExclusion(
                shareholder,
                request.getYear(),
                request.getBookingType(),
                request.isExclude()
        );
        return ResponseEntity.ok()
                .build();
    }
}
