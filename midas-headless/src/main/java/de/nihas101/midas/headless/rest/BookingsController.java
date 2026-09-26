package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.headless.rest.dto.BookingDto;
import de.nihas101.midas.headless.rest.dto.BookingsResponse;
import de.nihas101.midas.headless.rest.dto.OpeningBalanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingsController {

    private final BookingsService bookingsService;
    private final ShareholderLock shareholderLock;

    @GetMapping
    public ResponseEntity<BookingsResponse> getBookings(
            @RequestParam final Integer shareholderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate
    ) {
        Bookings bookings;
        if (startDate != null && endDate != null) {
            bookings = bookingsService.bookingsForShareholderAndDates(shareholderId, startDate, endDate);
        } else {
            return ResponseEntity.badRequest().build();
        }

        final OpeningBalance ob = bookings.openingBalance();
        final OpeningBalanceDto obDto = (ob != null && ob.getOpeningBalance() != null)
                ? OpeningBalanceDto.builder()
                .id(ob.getId())
                .shareholderId(ob.getShareholderId())
                .openingBalance(ob.getOpeningBalance())
                .year(ob.getYear())
                .source(ob.getSource())
                .build()
                : null;

        final List<BookingDto> bookingDtos = bookings.filter(b -> true).bookings().stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(BookingsResponse.builder()
                .openingBalance(obDto)
                .bookings(bookingDtos)
                .build());
    }

    @PostMapping("/check-duplicate")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestBody final BookingDto dto) {
        final DefaultBooking booking = toDomain(dto);
        final boolean exists = bookingsService.exists(booking);
        return ResponseEntity.ok(Map.of("duplicate", exists));
    }

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody final BookingDto dto,
            @RequestParam(defaultValue = "false") final boolean force
    ) {
        final Year bookingYear = Year.of(dto.getDate().getYear());
        shareholderLock.assertUnlocked(dto.getShareholderId(), bookingYear);

        final DefaultBooking booking = toDomain(dto);
        if (booking.getSource() == null) {
            booking.setSource(Source.USER);
        }

        if (!force && bookingsService.exists(booking)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("warning", "DUPLICATE_BOOKING", "message", "A booking with identical date, type, and comment already exists."));
        }

        bookingsService.create(booking);

        // Fetch back saved booking
        final List<Booking> currentBookings = bookingsService.bookingsForShareholderAndYear(dto.getShareholderId(), bookingYear)
                .filter(b -> true).bookings();
        final Booking created = currentBookings.stream()
                .filter(b -> dto.getComment().equals(b.getComment()) && dto.getDate().equals(b.getDate()))
                .reduce((first, second) -> second)
                .orElse(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDto> updateBooking(
            @PathVariable final Integer id,
            @RequestBody final BookingDto dto
    ) {
        dto.setId(id);
        final Year bookingYear = Year.of(dto.getDate().getYear());
        shareholderLock.assertUnlocked(dto.getShareholderId(), bookingYear);

        final DefaultBooking booking = toDomain(dto);
        bookingsService.update(booking);

        final List<Booking> currentBookings = bookingsService.bookingsForShareholderAndYear(dto.getShareholderId(), bookingYear)
                .filter(b -> true).bookings();
        final Booking updated = currentBookings.stream()
                .filter(b -> id.equals(b.getId()))
                .findFirst()
                .orElse(booking);

        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable final Integer id,
            @RequestParam final Integer shareholderId,
            @RequestParam final Integer year
    ) {
        shareholderLock.assertUnlocked(shareholderId, Year.of(year));

        final List<Booking> currentBookings = bookingsService.bookingsForShareholderAndYear(shareholderId, Year.of(year))
                .filter(b -> true).bookings();
        final Booking toDelete = currentBookings.stream()
                .filter(b -> id.equals(b.getId()))
                .findFirst()
                .orElse(null);

        if (toDelete == null) {
            return ResponseEntity.notFound().build();
        }

        bookingsService.delete(toDelete);
        return ResponseEntity.noContent().build();
    }

    private BookingDto toDto(final Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .displayId(b.getDisplayId())
                .shareholderId(b.getShareholderId())
                .date(b.getDate())
                .type(b.getType())
                .amount(b.getAmount())
                .comment(b.getComment())
                .source(b.getSource())
                .build();
    }

    private DefaultBooking toDomain(final BookingDto dto) {
        return DefaultBooking.builder()
                .id(dto.getId())
                .displayId(dto.getDisplayId())
                .shareholderId(dto.getShareholderId())
                .date(dto.getDate())
                .type(dto.getType())
                .amount(dto.getAmount())
                .comment(dto.getComment())
                .source(dto.getSource())
                .build();
    }
}
