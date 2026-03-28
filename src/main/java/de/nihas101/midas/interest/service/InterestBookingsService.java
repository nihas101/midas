package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.DefaultBookings;
import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class InterestBookingsService implements InterestBookingsWriter, InterestBookingsReader {

    private final BookingsService bookingsService;
    private final BookingsRepository bookingsRepository;
    private final ShareholdersRepository shareholdersRepository;
    private final OpeningBalanceRepository openingBalanceRepository;

    @Override
    public Booking systemGeneratedInterestForShareholderAndYear(final Shareholder shareholder, final Year year) {
        final LocalDate endOfYear = year.atMonth(Month.DECEMBER).atDay(31);

        return Booking.fromEntity(
                bookingsRepository.findFirstByShareholderAndDateAndTypeAndSource(
                        ShareholderEntity.fromDto(shareholder),
                        endOfYear,
                        BookingType.INTEREST,
                        Source.SYSTEM
                )
        );
    }

    @Override
    public Bookings interestRelatedBookingsForShareholderAndYear(final Integer shareholderId, final Year year) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        final LocalDate startOfYear = year.atMonth(Month.JANUARY).atDay(1);
        final LocalDate endOfYear = year.atMonth(Month.DECEMBER).atDay(31);

        final List<Booking> bookings = bookingsRepository.findByShareholderAndDateBetweenOrderByDateAsc(shareholder, startOfYear, endOfYear)
                .stream()
                .filter(bookingsAddedByUser()) // Exclude system generated interest, because that is what we will calculate
                .map(Booking::fromEntity)
                .toList();

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(shareholder, year.atDay(1))
                .map(OpeningBalance::fromEntity)
                .orElse(null);

        return new DefaultBookings(
                bookings,
                openingBalance
        );
    }

    private Predicate<BookingEntity> bookingsAddedByUser() {
        return bookingEntity -> !BookingType.INTEREST.equals(bookingEntity.getType())
                || !(Source.SYSTEM == bookingEntity.getSource());
    }

    @Override
    public void create(final Booking booking) {
        bookingsService.create(booking);
    }

    @Override
    public void update(final Booking booking) {
        bookingsService.update(booking);
    }

    @Override
    public void deleteInterestBooking(final Shareholder shareholder, final Year year) {
        bookingsRepository.deleteByShareholderAndDateAndSource(
                ShareholderEntity.fromDto(shareholder),
                year.atMonth(Month.DECEMBER).atEndOfMonth(),
                Source.SYSTEM
        );
    }
}
