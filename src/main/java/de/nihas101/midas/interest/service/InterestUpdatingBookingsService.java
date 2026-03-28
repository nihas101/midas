package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.repository.InterestRateRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestUpdatingBookingsService implements BookingsWriter { // TODO: Tests

    private final BookingsWriter delegate;
    private final InterestBookingsReader bookingsReader;
    private final ShareholdersRepository shareholdersRepository;
    private final InterestRateRepository interestRateRepository;

    @Override
    public void create(final Booking booking) {
        delegate.create(booking);
        updateInterest(booking);
    }

    @Override
    public void update(final Booking booking) {
        delegate.update(booking);
        updateInterest(booking);
    }

    @Override
    public void delete(final Booking booking) {
        delegate.delete(booking);
        updateInterest(booking);
    }

    private void updateInterest(final Booking booking) {
        final Year year = Year.of(booking.getDate().getYear());
        final ShareholderEntity shareholder = shareholdersRepository.getReferenceById(booking.getShareholderId());
        final Booking interestBooking = bookingsReader.systemGeneratedInterestForShareholderAndYear(Shareholder.fromEntity(shareholder), year);
        if (interestBooking == null) {
            // We only want to update the interest here, not create it
            return;
        }
        final Optional<InterestRate> interestRate = interestRateRepository.findByShareholderAndDate(shareholder, year.atMonth(Month.JANUARY).atDay(1))
                .map(InterestRate::fromEntity);
        if (interestRate.isEmpty()) {
            return;
        }

        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation interestCalculation = new InterestCalculation(
                bookings,
                year,
                interestRate.get().getInterestRate()
        );
        // TODO: This mutates the object! Handle this differently
        interestBooking.setAmount(interestCalculation.interest());
        delegate.update(interestBooking);
    }
}
