package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
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

    private final BookingsRepository bookingsRepository;
    private final ShareholdersRepository shareholdersRepository;
    private final OpeningBalanceRepository openingBalanceRepository;

    @Override
    public Bookings bookingsForShareholderAndYear(final Integer shareholderId, final Year year) {
        return bookingsForShareholderAndDates(
                shareholderId,
                year.atMonth(Month.JANUARY).atDay(1),
                year.atMonth(Month.DECEMBER).atEndOfMonth()
        );
    }

    @Override
    public Bookings bookingsForShareholderAndDates(
            final Integer shareholderId,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        final List<Booking> bookings = bookingsRepository.findByShareholderAndDateBetweenOrderByDateAsc(shareholder, startDate, endDate)
                .stream()
                .map(Booking::fromEntity)
                .toList();

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(
                        shareholder,
                        Year.of(startDate.getYear()).atMonth(Month.JANUARY).atDay(1)
                )
                .map(OpeningBalance::fromEntity)
                .orElse(null);

        return new DefaultBookings(
                bookings,
                openingBalance
        );
    }

    @Override
    public boolean exists(final Booking booking) {
        final ShareholderEntity shareholderEntity = shareholdersRepository.findById(booking.getShareholderId()).orElse(null);
        if (shareholderEntity == null) {
            return false;
        }
        return bookingsRepository.existsByShareholderAndDateAndTypeAndCommentAndIdNot(
                shareholderEntity,
                booking.getDate(),
                booking.getType(),
                booking.getComment(),
                booking.getId()
        );
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
