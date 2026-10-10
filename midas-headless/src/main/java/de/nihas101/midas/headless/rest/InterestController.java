package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.interest.InterestCalculationFactory;
import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.InterestRowService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.interest.service.bookingupdate.InterestUpdate;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.headless.rest.dto.InterestRateDto;
import de.nihas101.midas.headless.rest.dto.InterestResponse;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/interest")
@RequiredArgsConstructor
public class InterestController {

    private final InterestRateService interestRateService;
    private final ShareholdersService shareholdersService;
    private final BookingsService bookingsService;
    private final InterestBookingsReader interestBookingsReader;
    private final InterestRateRepository interestRateRepository;
    private final ShareholdersRepository shareholdersRepository;
    private final InterestCalculationFactory interestCalculationFactory;
    private final InterestRowService interestRowService;
    private final ShareholderLock shareholderLock;

    @GetMapping
    public ResponseEntity<InterestResponse> getInterest(
            @RequestParam final Integer shareholderId,
            @RequestParam(name = "year") final Integer yearValue,
            @RequestParam final Locale locale
    ) {
        final Year year = Year.of(yearValue);
        final Shareholder sh = shareholdersService.shareholder(shareholderId);
        if (sh == null) {
            return ResponseEntity.notFound().build();
        }

        final InterestRate rate = interestRateService.interestRate(shareholderId, year);
        final InterestRateDto rateDto = rate != null ? toDto(rate) : null;

        List<InterestCalculationRow> rows = Collections.emptyList();
        if (rate != null) {
            final Bookings bookings = bookingsService.bookingsForShareholderAndYear(shareholderId, year);
            final InterestCalculation calculation = interestCalculationFactory.create(
                    bookings,
                    year,
                    rate.getInterestRate()
            );
            rows = interestRowService.generateRows(
                    year,
                    bookings,
                    rate.getInterestRate(),
                    calculation,
                    locale
            );
        }

        return ResponseEntity.ok(InterestResponse.builder()
                .interestRate(rateDto)
                .rows(rows)
                .build());
    }

    @PostMapping
    public ResponseEntity<InterestRateDto> setInterestRate(@RequestBody final InterestRateDto dto) {
        final Year year = dto.getYear();
        shareholderLock.assertUnlocked(dto.getShareholderId(), year);

        final InterestRate existing = interestRateService.interestRate(dto.getShareholderId(), year);
        final InterestRate toSave = new InterestRate(
                existing != null ? existing.getId() : null,
                dto.getShareholderId(),
                dto.getInterestRate(),
                year
        );

        if (existing == null) {
            interestRateService.create(toSave);
        } else {
            interestRateService.update(toSave);
        }

        if (dto.isUpdateInterest()) {
            final ShareholderEntity shareholderEntity = shareholdersRepository.getReferenceById(dto.getShareholderId());
            final Booking interestBooking = interestBookingsReader.systemGeneratedInterestForShareholderAndYear(
                    DefaultShareholder.fromEntity(shareholderEntity),
                    year
            );
            final InterestUpdate interestUpdate = new InterestUpdate(
                    interestBookingsReader,
                    interestRateRepository,
                    bookingsService
            );
            interestUpdate.trigger(interestBooking, shareholderEntity, year);
        }

        final InterestRate saved = interestRateService.interestRate(dto.getShareholderId(), year);
        return ResponseEntity.ok(toDto(saved));
    }

    private InterestRateDto toDto(final InterestRate rate) {
        return InterestRateDto.builder()
                .id(rate.getId())
                .shareholderId(rate.getShareholderId())
                .interestRate(rate.getInterestRate())
                .year(rate.getYear())
                .build();
    }
}
