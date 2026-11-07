package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.headless.rest.dto.BookingsResponse;
import de.nihas101.midas.headless.rest.dto.RequestBooking;
import de.nihas101.midas.headless.rest.dto.ResponseBooking;
import de.nihas101.midas.headless.rest.dto.ResponseOpeningBalance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Controller for bookings CRUD")
public class BookingsController {

    private final BookingsReader bookingsReader;
    private final BookingsWriter bookingsWriter;

    @Operation(
            summary = "Get bookings for a shareholder and year",
            description = "Returns a list of bookings for the specified shareholder and year."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Invalid request parameter\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Internal Server Error\"}"
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<BookingsResponse> getBookings(
            @NotNull
            @Positive
            @RequestParam final Integer shareholderId,

            @NotNull
            @Positive
            @RequestParam(required = false) final Integer year
    ) {
        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholderId, Year.of(year));
        final ResponseOpeningBalance openingBalance = Optional.of(bookings)
                .map(Bookings::openingBalance)
                .map(ResponseOpeningBalance::toDto)
                .orElse(null);
        return ResponseEntity.ok(
                BookingsResponse.builder()
                        .openingBalance(openingBalance)
                        .bookings(bookings.map(ResponseBooking::toDto))
                        .build()
        );
    }

    @Operation(
            summary = "Create a booking for a shareholder and year",
            description = "Creates a booking for the specified shareholder and year."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Booking created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Invalid request parameter\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate booking detected",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"warning\":\"DUPLICATE_BOOKING\",\"message\":\"A booking with identical date, type, and comment already exists.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Internal Server Error\"}"
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createBooking(
            @NotNull
            @RequestBody final RequestBooking booking,

            @RequestParam(defaultValue = "false") final boolean force
    ) {
        final Booking domainBooking = booking.toDomain();
        if (!force && bookingsReader.exists(domainBooking)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "warning", "DUPLICATE_BOOKING",
                                    "message", "A booking with identical date, type, and comment already exists."
                            )
                    );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseBooking.toDto(
                        bookingsWriter.create(domainBooking)
                )
        );
    }

    @Operation(
            summary = "Update a booking for a shareholder and year",
            description = "Updates a booking for the specified shareholder and year."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Invalid request parameter\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Internal Server Error\"}"
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseBooking> updateBooking(
            @PathVariable
            @NotNull
            @Positive final Integer id,

            @RequestBody
            @NotNull final RequestBooking booking
    ) {
        return ResponseEntity.ok(
                ResponseBooking.toDto(
                        bookingsWriter.update(
                                booking.toDomain(id)
                        )
                )
        );
    }

    @Operation(
            summary = "Delete a booking for a shareholder and year",
            description = "Deletes a booking for the specified shareholder and year."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Booking deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Internal Server Error\"}"
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
            @NotNull
            @Positive
            @PathVariable final Integer id,

            @NotNull
            @Positive
            @RequestParam final Integer shareholderId,

            @NotNull
            @Positive
            @RequestParam final Integer year
    ) {
        final Booking toDelete = bookingsReader.bookingForId(id);
        if (toDelete == null) {
            return ResponseEntity.notFound()
                    .build();
        }
        bookingsWriter.delete(toDelete);
        return ResponseEntity.noContent()
                .build();
    }
}
