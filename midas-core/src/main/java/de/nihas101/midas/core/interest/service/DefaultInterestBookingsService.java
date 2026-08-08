package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestBookingsService;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.bookings.dto.DefaultBookings;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.persistance.bookings.BookingEntity;
import de.nihas101.midas.persistance.bookings.BookingsRepository;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class DefaultInterestBookingsService implements InterestBookingsService {

    private final BookingsService bookingsService;
    private final BookingsRepository bookingsRepository;
    private final ShareholdersRepository shareholdersRepository;
    private final OpeningBalanceRepository openingBalanceRepository;

    @Override
    public Booking systemGeneratedInterestForShareholderAndYear(final Shareholder shareholder, final Year year) {
        final LocalDate endOfYear = year.atMonth(Month.DECEMBER).atDay(31);

        return DefaultBooking.fromEntity(
                bookingsRepository.findFirstByShareholderAndDateAndTypeAndSource(
                        DefaultShareholder.fromDto(shareholder),
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
                .map(DefaultBooking::fromEntity)
                .toList();

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(shareholder, year.atDay(1))
                .map(DefaultOpeningBalance::fromEntity)
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
                DefaultShareholder.fromDto(shareholder),
                year.atMonth(Month.DECEMBER).atEndOfMonth(),
                Source.SYSTEM
        );
    }
}
