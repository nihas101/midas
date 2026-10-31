package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.interest.InterestCalculationFactory;
import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.api.interest.InterestRateService;
import de.nihas101.midas.api.interest.InterestRowService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.interest.dto.DefaultInterestRate;
import de.nihas101.midas.headless.config.MidasConfig;
import de.nihas101.midas.headless.rest.dto.CreateInterestRate;
import de.nihas101.midas.headless.rest.dto.InterestResponse;
import de.nihas101.midas.headless.rest.dto.RequestToggleAutomaticInterestBooking;
import de.nihas101.midas.headless.rest.dto.ResponseInterestRate;
import de.nihas101.midas.headless.rest.dto.UpdateInterestRate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/api/v1/interest")
@RequiredArgsConstructor
@Tag(name = "Interest", description = "Controller for calculating interest for a year and shareholder.")
public class InterestController {

    private final InterestRateService interestRateService;
    private final ShareholdersReader shareholdersReader;
    private final BookingsReader bookingsReader;
    private final BookingsWriter bookingsWriter;
    private final InterestBookingsReader interestBookingsReader;
    private final InterestCalculationFactory interestCalculationFactory;
    private final InterestRowService interestRowService;
    private final MessageSource messageSource;
    private final BookingFactory bookingFactory;
    private final MidasConfig config;

    @Operation(
            summary = "Get the calculated interest for a shareholder and year",
            description = "Returns the calculated interest and calculation rows for the specified shareholder and year."
    )
    @GetMapping
    public ResponseEntity<InterestResponse> getInterest(
            @NotNull
            @Positive
            @RequestParam final Integer shareholderId,

            @NotNull
            @Positive
            @RequestParam final Integer year,

            final Locale locale
    ) {
        final Year y = Year.of(year);
        final InterestRate interestRate = interestRateService.interestRate(shareholderId, y);
        if (interestRate == null) {
            return ResponseEntity.ok(
                    InterestResponse.builder()
                            .interestRate(null)
                            .rows(emptyList())
                            .build()
            );
        }

        final Bookings bookings = interestBookingsReader.interestRelatedBookingsForShareholderAndYear(shareholderId, y);
        final InterestCalculation calculation = interestCalculationFactory.create(
                bookings,
                y,
                interestRate.getInterestRate()
        );

        final Locale loc = locale != null ? locale : Locale.of(config.getI18n().getDefaultLocale());
        final List<InterestCalculationRow> rows = interestRowService.generateRows(
                y,
                bookings,
                interestRate.getInterestRate(),
                calculation,
                loc
        );

        return ResponseEntity.ok(
                InterestResponse.builder()
                        .interestRate(ResponseInterestRate.toDto(interestRate))
                        .rows(rows)
                        .build()
        );
    }

    @Operation(
            summary = "Create an interest rate for a shareholder and year",
            description = "Creates an interest rate for the specified shareholder and year."
    )
    @PostMapping
    public ResponseEntity<ResponseInterestRate> createInterestRate(
            @NotNull
            @RequestBody final CreateInterestRate createRequest
    ) {
        final InterestRate toSave = new DefaultInterestRate(
                null,
                createRequest.getShareholderId(),
                createRequest.getInterestRate(),
                createRequest.getYear()
        );

        final InterestRate saved = interestRateService.create(toSave);
        return ResponseEntity.ok(ResponseInterestRate.toDto(saved));
    }

    @Operation(
            summary = "Toggle the automatic generation of the interest booking",
            description = "Toggles weather or not a booking for the interest should be generated automatically."
    )
    @PostMapping("/booking")
    public ResponseEntity<Void> toggleInterestBooking(
            @NotNull
            @RequestBody final RequestToggleAutomaticInterestBooking request,

            final Locale locale
    ) {
        final Year year = request.getYear();
        final Integer shareholderId = request.getShareholderId();

        final Shareholder shareholder = shareholdersReader.shareholder(shareholderId);
        final Booking booking = interestBookingsReader.systemGeneratedInterestForShareholderAndYear(shareholder, year);
        if (booking != null) {
            booking.setSource(request.getSource());
            bookingsWriter.update(booking);
        } else if (request.isToggleAutomaticBooking()) {
            final InterestRate interestRate = interestRateService.interestRate(shareholderId, year);
            createInterestBooking(shareholderId, locale, year, interestRate);
        }

        return ResponseEntity.ok()
                .build();
    }

    @Operation(
            summary = "Update the interest rate of a shareholder and year",
            description = "Updates the interest rate of the specified shareholder and year."
    )
    @PutMapping
    public ResponseEntity<ResponseInterestRate> updateInterestRate(
            @NotNull
            @RequestBody final UpdateInterestRate updateRequest
    ) {
        final Year year = updateRequest.getYear();

        final InterestRate existing = interestRateService.interestRate(updateRequest.getShareholderId(), year);
        final InterestRate toSave = new DefaultInterestRate(
                existing != null ? existing.getId() : null,
                updateRequest.getShareholderId(),
                updateRequest.getInterestRate(),
                year
        );

        final InterestRate interestRate = interestRateService.update(toSave);
        return ResponseEntity.ok(ResponseInterestRate.toDto(interestRate));
    }

    private void createInterestBooking(
            final Integer shareholderId,
            final Locale locale,
            final Year year,
            final InterestRate interestRate
    ) {
        final Locale loc = locale != null ? locale : Locale.of(config.getI18n().getDefaultLocale());
        final Bookings bookings = interestBookingsReader.interestRelatedBookingsForShareholderAndYear(
                shareholderId,
                year
        );
        final MoneyAmount interest = interestCalculationFactory.create(
                bookings,
                year,
                interestRate.getInterestRate()
        ).interest();
        final Booking newBooking = bookingFactory.createSystemGeneratedInterestBooking(
                shareholderId,
                year,
                interest,
                messageSource.getMessage("bookings.type.interest", null, loc)
        );
        bookingsWriter.create(newBooking);
    }
}
