package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.repository.InterestRateRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestUpdatingBookingsService implements BookingsWriter { // TODO: Tests

    private final InterestBookingsReader bookingsReader;
    private final BookingsWriter delegate;
    private final ShareholdersRepository shareholdersRepository;
    private final InterestRateRepository interestRateRepository;
    private final MessageSource messageSource;
    private final MidasLocaleResolver localeResolver;

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
        final ShareholderEntity shareholder = shareholdersRepository.getReferenceById(booking.getShareholderId());
        final Year year = Year.of(booking.getDate().getYear());
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

        final Booking interestBooking = bookingsReader.systemGeneratedInterestForShareholderAndYear(shareholder.getId(), year);
        // TODO: This logic is duplicated in multiple places, extract into class to keep in-sync
        if (interestBooking != null) {
            // TODO: This mutates the object! Handle this differently
            interestBooking.setAmount(interestCalculation.interest());
            delegate.update(interestBooking);
        } else {
            final Booking newBooking = new Booking(
                    null,
                    null,
                    shareholder.getId(),
                    year.atMonth(Month.DECEMBER).atEndOfMonth(),
                    BookingType.INTEREST,
                    interestCalculation.interest(),
                    messageSource.getMessage("bookings.type.interest", null, localeResolver.resolve()),
                    Source.SYSTEM
            );
            delegate.create(newBooking);
        }
    }
}
