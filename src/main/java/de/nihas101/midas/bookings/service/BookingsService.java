package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.CachingBookings;
import de.nihas101.midas.bookings.dto.DefaultBookings;
import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingsService implements BookingsWriter, BookingsReader {

    // TODO: Use the other services? instead of the raw repository
    // TOOD: Get rid of the services in favour of wrappers that are applied here that handle the mappings?
    private final BookingsRepository bookingsRepository;
    private final ShareholdersRepository shareholdersRepository;
    private final OpeningBalanceRepository openingBalanceRepository;

    @Override
    public Bookings bookingsForShareholderAndYear(final Integer shareholderId, final Integer year) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found")); // TODO: i18n

        final LocalDate start = LocalDate.of(year, Month.JANUARY, 1);
        final LocalDate end = LocalDate.of(year, Month.DECEMBER, 31);

        final List<Booking> bookings = bookingsRepository.findByShareholderAndDateBetweenOrderByDateAsc(shareholder, start, end)
                .stream()
                .map(Booking::fromEntity)
                .toList();

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(shareholder, Year.of(year).atDay(1))
                .map(OpeningBalance::fromEntity)
                .orElse(null);

        return new CachingBookings(
                new DefaultBookings(
                        bookings,
                        openingBalance
                )
        );
    }

    @Transactional
    @Override
    public void create(final Booking booking) {
        if (booking.getId() != null) {
            throw new IllegalArgumentException("BookingService#create with booking.getId() != null"); // TODO: i18n
        }
        upsertEntity(booking);
    }

    @Transactional
    @Override
    public void update(final Booking booking) {
        if (booking.getId() == null) {
            throw new IllegalArgumentException("BookingService#udpate with booking.getId() == null"); // TODO: i18n
        }

        upsertEntity(booking);
    }

    private void upsertEntity(final Booking booking) {
        ShareholderEntity shareholder = shareholdersRepository.findById(booking.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found")); // TODO: i18n
        bookingsRepository.save(BookingEntity.fromDto(booking, shareholder));
    }

    public void delete(final Booking booking) {
        ShareholderEntity shareholder = shareholdersRepository.findById(booking.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found")); // TODO: i18n
        bookingsRepository.delete(BookingEntity.fromDto(booking, shareholder));
    }
}
