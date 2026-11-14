package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestBookingsWriter;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.interest.InterestCalculationFactory;
import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.api.interest.InterestRateService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@Primary // TODO: Also need to consider lock beforehand
@RequiredArgsConstructor
public class InterestBookingUpdatingInterestRateService implements InterestRateService {

    private final InterestCalculationFactory interestCalculationFactory;
    private final InterestBookingsReader bookingsReader;
    private final InterestBookingsWriter bookingsWriter;
    private final ShareholdersReader shareholdersReader;
    private final InterestRateService delegate;

    @Override
    public InterestRate interestRate(final Integer shareholderId, final Year year) {
        return delegate.interestRate(shareholderId, year);
    }

    @Override
    public InterestRate create(final InterestRate interestRate) {
        final InterestRate ir = delegate.create(interestRate);
        updateInterestBooking(interestRate);
        return ir;
    }

    @Override
    public InterestRate update(final InterestRate interestRate) {
        final InterestRate ir = delegate.update(interestRate);
        updateInterestBooking(interestRate);
        return ir;
    }

    private void updateInterestBooking(final InterestRate interestRate) {
        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(interestRate.getShareholderId(), interestRate.getYear());
        final InterestCalculation interestCalculation = interestCalculationFactory.create(
                bookings,
                interestRate.getYear(),
                interestRate.getInterestRate()
        );

        final Shareholder shareholder = shareholdersReader.shareholder(interestRate.getShareholderId());
        final Year year = interestRate.getYear();
        final Booking booking = bookingsReader.systemGeneratedInterestForShareholderAndYear(
                shareholder,
                year
        );
        if (booking != null) {
            booking.setAmount(interestCalculation.interest());
            bookingsWriter.update(booking);
        }
    }
}
