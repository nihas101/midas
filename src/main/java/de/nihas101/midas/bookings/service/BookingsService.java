package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.DefaultBookings;
import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

// TODO: Break this service up into multiple ones -> One method per class?
@Service
@RequiredArgsConstructor
public class BookingsService implements BookingsWriter, BookingsReader {

    private final BookingsRepository bookingsRepository;
    private final ShareholdersRepository shareholdersRepository;
    private final OpeningBalanceRepository openingBalanceRepository;

    // TODO: Can be extracted into separate class
    // -- INTEREST RELATED --
    @Override
    public Booking systemGeneratedInterestForShareholderAndYear(final Integer shareholderId, final Year year) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        final LocalDate endOfYear = year.atMonth(Month.DECEMBER).atDay(31);

        return Booking.fromEntity(
                bookingsRepository.findFirstByShareholderAndDateAndTypeAndSource(
                        shareholder,
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
                // Exclude the interest, because that is what we will calculate
                .filter(bookingEntity -> !BookingType.INTEREST.equals(bookingEntity.getType()))
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

    // TODO: Can be extracted into separate class
    // -- BOOKING View related --
    @Override
    public Bookings bookingsForShareholderAndYear(final Integer shareholderId, final Year year) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found")); // TODO: i18n

        final LocalDate startOfYear = year.atMonth(Month.JANUARY).atDay(1);
        final LocalDate endOfYear = year.atMonth(Month.DECEMBER).atEndOfMonth();

        final List<Booking> bookings = bookingsRepository.findByShareholderAndDateBetweenOrderByDateAsc(shareholder, startOfYear, endOfYear)
                .stream()
                .map(Booking::fromEntity)
                .toList();

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(shareholder, year.atMonth(Month.JANUARY).atDay(1))
                .map(OpeningBalance::fromEntity)
                .orElse(null);

        return new DefaultBookings(
                bookings,
                openingBalance
        );
    }

    @Override
    public boolean exists(final Booking booking) {
        return shareholdersRepository.findById(booking.getShareholderId())
                .filter(shareholder -> bookingsRepository.exists(
                        Example.of(
                                new BookingEntity(
                                        null,
                                        null,
                                        shareholder,
                                        booking.getDate(),
                                        booking.getType(),
                                        null,
                                        booking.getComment(),
                                        null
                                )
                        )
                )).isPresent();
    }

    @Transactional
    @Override
    public void create(final Booking booking) {
        if (booking.getId() != null) { // TODO: Move this into a wrapper, so this always happens first
            throw new IllegalArgumentException("BookingService#create with booking.getId() != null");
        }
        upsertEntity(booking);
    }

    @Transactional
    @Override
    public void update(final Booking booking) {
        if (booking.getId() == null) {
            throw new IllegalArgumentException("BookingService#udpate with booking.getId() == null");
        }

        upsertEntity(booking);
    }

    private void upsertEntity(final Booking booking) {
        ShareholderEntity shareholder = shareholdersRepository.findById(booking.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));
        bookingsRepository.save(BookingEntity.fromDto(booking, shareholder));
    }

    public void delete(final Booking booking) {
        ShareholderEntity shareholder = shareholdersRepository.findById(booking.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));
        bookingsRepository.delete(BookingEntity.fromDto(booking, shareholder));
    }
}
