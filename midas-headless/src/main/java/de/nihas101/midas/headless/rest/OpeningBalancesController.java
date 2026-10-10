package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.interest.service.openingbalanceupdate.DefaultInterestUpdatingOpeningBalanceService;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.headless.rest.dto.CarryOverRequest;
import de.nihas101.midas.headless.rest.dto.OpeningBalanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/opening-balances")
@RequiredArgsConstructor
public class OpeningBalancesController {

    private final DefaultInterestUpdatingOpeningBalanceService openingBalanceService;
    private final BookingsService bookingsService;
    private final ShareholderLock shareholderLock;

    @GetMapping
    public ResponseEntity<OpeningBalanceDto> getOpeningBalance(
            @RequestParam final Integer shareholderId,
            @RequestParam final Integer year
    ) {
        final OpeningBalance ob = openingBalanceService.openingBalance(shareholderId, Year.of(year));
        if (ob == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(ob));
    }

    @PostMapping
    public ResponseEntity<OpeningBalanceDto> setOpeningBalance(@RequestBody final OpeningBalanceDto dto) {
        shareholderLock.assertUnlocked(dto.getShareholderId(), dto.getYear());

        final OpeningBalance existing = openingBalanceService.openingBalance(dto.getShareholderId(), dto.getYear());
        final Source source = dto.getSource() != null ? dto.getSource() : Source.USER;
        final DefaultOpeningBalance balance = new DefaultOpeningBalance(
                existing != null ? existing.getId() : null,
                dto.getShareholderId(),
                dto.getOpeningBalance(),
                dto.getYear(),
                source
        );

        if (existing == null) {
            openingBalanceService.create(balance);
        } else {
            openingBalanceService.update(balance);
        }

        final OpeningBalance saved = openingBalanceService.openingBalance(dto.getShareholderId(), dto.getYear());
        return ResponseEntity.ok(toDto(saved));
    }

    @PostMapping("/carry-over")
    public ResponseEntity<?> carryOverToNextYear(@RequestBody final CarryOverRequest request) {
        final Year nextYear = request.getFromYear().plusYears(1);
        shareholderLock.assertUnlocked(request.getShareholderId(), nextYear);

        final OpeningBalance nextYearBalance = openingBalanceService.openingBalance(request.getShareholderId(), nextYear);

        // If toggling carry over off (active == false)
        if (Boolean.FALSE.equals(request.getActive())) {
            if (nextYearBalance != null) {
                final DefaultOpeningBalance userBalance = new DefaultOpeningBalance(
                        nextYearBalance.getId(),
                        request.getShareholderId(),
                        nextYearBalance.getOpeningBalance(),
                        nextYear,
                        Source.USER
                );
                openingBalanceService.update(userBalance);
                return ResponseEntity.ok(toDto(openingBalanceService.openingBalance(request.getShareholderId(), nextYear)));
            }
            return ResponseEntity.ok().build();
        }

        // Calculate closing balance of fromYear
        final Bookings bookings = bookingsService.bookingsForShareholderAndYear(request.getShareholderId(), request.getFromYear());
        MoneyAmount closingBalance = MoneyAmount.ZERO;
        if (bookings.openingBalance() != null && bookings.openingBalance().getOpeningBalance() != null) {
            closingBalance = closingBalance.plus(bookings.openingBalance().getOpeningBalance());
        }
        for (var b : bookings.filter(x -> true).bookings()) {
            closingBalance = closingBalance.plus(b.getAmount());
        }

        // Check if next year already has an opening balance and overwrite wasn't forced
        if (nextYearBalance != null && !request.isForceOverwrite() && Source.USER.equals(nextYearBalance.getSource())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "warning", "OPENING_BALANCE_EXISTS",
                            "message", "Opening balance for " + nextYear + " already exists. Overwrite?"
                    ));
        }

        final DefaultOpeningBalance newNextYearBalance = new DefaultOpeningBalance(
                nextYearBalance != null ? nextYearBalance.getId() : null,
                request.getShareholderId(),
                closingBalance,
                nextYear,
                Source.SYSTEM
        );

        if (nextYearBalance == null) {
            openingBalanceService.create(newNextYearBalance);
        } else {
            openingBalanceService.update(newNextYearBalance);
        }

        final OpeningBalance saved = openingBalanceService.openingBalance(request.getShareholderId(), nextYear);
        return ResponseEntity.ok(toDto(saved));
    }

    private OpeningBalanceDto toDto(final OpeningBalance ob) {
        return OpeningBalanceDto.builder()
                .id(ob.getId())
                .shareholderId(ob.getShareholderId())
                .openingBalance(ob.getOpeningBalance())
                .year(ob.getYear())
                .source(ob.getSource())
                .build();
    }
}
