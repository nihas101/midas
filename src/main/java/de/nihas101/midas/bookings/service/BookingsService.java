package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.CachingBookings;
import de.nihas101.midas.bookings.dto.DefaultBookings;
import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingsService implements BookingsWriter, BookingsReader {

    private final BookingsRepository bookingsRepository;
    private final ShareholdersRepository shareholdersRepository;

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
        return new CachingBookings(
                new DefaultBookings(
                        bookings
                )
        );
    }

    @Transactional
    @Override
    public void create(final Booking booking) {
        if (booking.getId() != null) {
            throw new IllegalArgumentException("BookingService#create with booking.getId() != null"); // TODO: i18n
        }
        ShareholderEntity shareholder = shareholdersRepository.findById(booking.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found")); // TODO: i18n
        BookingEntity entity = BookingEntity.fromDto(booking, shareholder);
        bookingsRepository.save(entity);
    }

    @Transactional
    @Override
    public void update(final Booking booking) {
        if (booking.getId() == null) {
            throw new IllegalArgumentException("BookingService#udpate with booking.getId() == null"); // TODO: i18n
        }
        final ShareholderEntity shareholder = shareholdersRepository.findById(booking.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found")); // TODO: i18n

        final BookingEntity entity = booking.getId() != null ?
                bookingsRepository.findById(booking.getId()).orElse(new BookingEntity()) :
                new BookingEntity();

        entity.setId(booking.getId());
        entity.setShareholder(shareholder);
        entity.setDate(booking.getDate());
        entity.setType(booking.getType());
        entity.setAmount(booking.getAmount());
        entity.setComment(booking.getComment());

        bookingsRepository.save(entity);
    }
}
