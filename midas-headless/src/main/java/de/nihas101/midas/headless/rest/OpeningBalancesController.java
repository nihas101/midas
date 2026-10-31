package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.openingbalance.OpeningBalanceService;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.headless.rest.dto.CarryOverRequest;
import de.nihas101.midas.headless.rest.dto.RequestOpeningBalance;
import de.nihas101.midas.headless.rest.dto.ResponseOpeningBalance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/opening-balances")
@RequiredArgsConstructor
@Tag(name = "Opening Balances", description = "Controller for opening balances CRUD")
public class OpeningBalancesController {

    private final OpeningBalanceService openingBalanceService;
    private final BookingsReader bookingsReader;

    @Operation(
            summary = "Get the opening balance for a shareholder and year",
            description = "Returns the opening balance for the specified shareholder and year."
    )
    @GetMapping
    public ResponseEntity<ResponseOpeningBalance> getOpeningBalance(
            @NotNull
            @Positive
            @RequestParam final Integer shareholderId,

            @NotNull
            @Positive
            @RequestParam final Integer year
    ) {
        final OpeningBalance balance = openingBalanceService.openingBalance(shareholderId, Year.of(year));
        if (balance == null) {
            return ResponseEntity.notFound()
                    .build();
        }
        return ResponseEntity.ok(ResponseOpeningBalance.toDto(balance));
    }

    @Operation(
            summary = "Create an opening balance for a shareholder and year",
            description = "Creates an opening balance for the specified shareholder and year."
    )
    @PostMapping
    public ResponseEntity<ResponseOpeningBalance> createOpeningBalance(
            @NotNull
            @RequestBody final RequestOpeningBalance openingBalance
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseOpeningBalance.toDto(
                        openingBalanceService.create(
                                openingBalance.toDomain()
                        )
                )
        );
    }

    @Operation(
            summary = "Update an opening balance for a shareholder and year",
            description = "Updates an opening balance for the specified shareholder and year."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseOpeningBalance> updateOpeningBalance(
            @NotNull
            @Positive
            @PathVariable final Integer id,

            @NotNull
            @RequestBody final RequestOpeningBalance openingBalance
    ) {
        return ResponseEntity.ok(
                ResponseOpeningBalance.toDto(
                        openingBalanceService.update(openingBalance.toDomain(id))
                )
        );
    }

    @Operation(
            summary = "Check carry over status for a shareholder and year",
            description = "Checks whether the closing balance of the given year is automatically carried over to next year."
    )
    @GetMapping("/carry-over")
    public ResponseEntity<Map<String, Boolean>> getCarryOverStatus(
            @NotNull
            @Positive
            @RequestParam final Integer shareholderId,

            @NotNull
            @Positive
            @RequestParam final Integer fromYear
    ) {
        final Year nextYear = Year.of(fromYear).plusYears(1);
        final OpeningBalance nextYearBalance = openingBalanceService.openingBalance(shareholderId, nextYear);
        final boolean active = nextYearBalance != null && Source.SYSTEM.equals(nextYearBalance.getSource());
        return ResponseEntity.ok(Map.of("active", active));
    }

    @Operation(
            summary = "Carry over a closing balance to next year",
            description = "Toggles whether or not the closing balance of this year should be carried over as opening balance in the next year."
    )
    @PostMapping("/carry-over")
    public ResponseEntity<?> carryOverToNextYear(
            @NotNull
            @RequestBody final CarryOverRequest request
    ) {
        final Year nextYear = request.getFromYear().plusYears(1);
        final OpeningBalance nextYearBalance = openingBalanceService.openingBalance(request.getShareholderId(), nextYear);

        // If toggling carry over off (active == false)
        if (!request.isActive()) {
            if (nextYearBalance != null) {
                final DefaultOpeningBalance userBalance = new DefaultOpeningBalance(
                        nextYearBalance.getId(),
                        request.getShareholderId(),
                        nextYearBalance.getOpeningBalance(),
                        nextYear,
                        Source.USER
                );
                openingBalanceService.update(userBalance);
                return ResponseEntity.ok(
                        ResponseOpeningBalance.toDto(
                                openingBalanceService.openingBalance(request.getShareholderId(), nextYear)
                        )
                );
            }
            return ResponseEntity.ok()
                    .build();
        }

        // Check if next year already has an opening balance and overwrite wasn't forced
        if (nextYearBalance != null && !request.isForceOverwrite() && Source.USER.equals(nextYearBalance.getSource())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "warning", "OPENING_BALANCE_EXISTS",
                            "message", "Opening balance for " + nextYear + " already exists. Overwrite?"
                    ));
        }

        final OpeningBalance newNextYearBalance = new DefaultOpeningBalance(
                nextYearBalance != null ? nextYearBalance.getId() : null,
                request.getShareholderId(),
                calculateClosingBalance(
                        request.getShareholderId(),
                        request.getFromYear()
                ),
                nextYear,
                Source.SYSTEM
        );

        return ResponseEntity.ok(
                ResponseOpeningBalance.toDto(
                        upsertOpeningBalance(nextYearBalance, newNextYearBalance)
                )
        );
    }

    private MoneyAmount calculateClosingBalance(final Integer shareholderId, final Year year) {
        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholderId, year);
        MoneyAmount closingBalance = MoneyAmount.ZERO;
        final OpeningBalance openingBalance = bookings.openingBalance();
        if (openingBalance != null && openingBalance.getOpeningBalance() != null) {
            closingBalance = closingBalance.plus(openingBalance.getOpeningBalance());
        }

        return bookings.reduce(
                closingBalance,
                (balance, booking) -> balance.plus(booking.getAmount()),
                MoneyAmount::plus
        );
    }

    private OpeningBalance upsertOpeningBalance(
            final OpeningBalance existing,
            final OpeningBalance newBalance
    ) {
        if (existing == null) {
            return openingBalanceService.create(newBalance);
        } else {
            return openingBalanceService.update(newBalance);
        }
    }
}
