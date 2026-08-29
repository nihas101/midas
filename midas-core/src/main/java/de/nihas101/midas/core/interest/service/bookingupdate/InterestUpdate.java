package de.nihas101.midas.core.interest.service.bookingupdate;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.core.interest.DefaultInterestCalculation;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Optional;

public class InterestUpdate {
    private final InterestBookingsReader bookingsReader;
    private final InterestRateRepository interestRateRepository;
    private final BookingsWriter bookingsWriter;

    public InterestUpdate(
            final InterestBookingsReader bookingsReader,
            final InterestRateRepository interestRateRepository,
            final BookingsWriter bookingsWriter
    ) {
        this.bookingsReader = bookingsReader;
        this.interestRateRepository = interestRateRepository;
        this.bookingsWriter = bookingsWriter;
    }

    public void trigger(
            final Booking interestBooking,
            final ShareholderEntity shareholder,
            final Year year
    ) {
        if (interestBooking == null) {
            // We only want to update the interest here, not create it
            return;
        }
        final Optional<InterestRate> interestRate = interestRate(shareholder, year);
        if (interestRate.isEmpty()) {
            return;
        }

        final InterestCalculation interestCalculation = interestCalculation(shareholder, year, interestRate.get());
        interestBooking.setAmount(interestCalculation.interest());
        bookingsWriter.update(interestBooking);
    }

    private Optional<InterestRate> interestRate(final ShareholderEntity shareholder, final Year year) {
        final LocalDate date = year.atMonth(Month.JANUARY).atDay(1);
        return interestRateRepository.findByShareholderAndDate(shareholder, date)
                .map(InterestRate::fromEntity);
    }

    private InterestCalculation interestCalculation(
            final ShareholderEntity shareholder,
            final Year year,
            final InterestRate interestRate
    ) {
        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        return new DefaultInterestCalculation(
                bookings,
                year,
                interestRate.getInterestRate()
        );
    }
}