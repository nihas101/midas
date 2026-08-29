package de.nihas101.midas.core.interest.service.openingbalanceupdate;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.openingbalance.OpeningBalanceService;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.interest.service.bookingupdate.InterestUpdate;
import de.nihas101.midas.core.openingbalance.service.DefaultOpeningBalanceService;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultInterestUpdatingOpeningBalanceService implements InterestUpdatingOpeningBalanceService {

    private final OpeningBalanceService delegate;
    private final InterestBookingsReader bookingsReader;
    private final ShareholdersRepository shareholdersRepository;
    private final InterestUpdate interestUpdate;

    @Autowired
    public DefaultInterestUpdatingOpeningBalanceService(
            final DefaultOpeningBalanceService delegate,
            final BookingsService bookingsWriter,
            final InterestBookingsReader bookingsReader,
            final ShareholdersRepository shareholdersRepository,
            final InterestRateRepository interestRateRepository
    ) {
        this.delegate = delegate;
        this.bookingsReader = bookingsReader;
        this.shareholdersRepository = shareholdersRepository;
        this.interestUpdate = new InterestUpdate(
                bookingsReader,
                interestRateRepository,
                bookingsWriter
        );
    }

    @Override
    public OpeningBalance openingBalance(final Integer shareholderId, final Year year) {
        return delegate.openingBalance(shareholderId, year);
    }

    @Override
    public void create(final OpeningBalance openingBalance) {
        delegate.create(openingBalance);
        updateInterest(openingBalance);
    }

    @Override
    public void update(final OpeningBalance openingBalance) {
        delegate.update(openingBalance);
        updateInterest(openingBalance);
    }

    private void updateInterest(final OpeningBalance openingBalance) {
        final Year year = openingBalance.getYear();
        final ShareholderEntity shareholder = shareholdersRepository.getReferenceById(openingBalance.getShareholderId());
        final Booking interestBooking = bookingsReader.systemGeneratedInterestForShareholderAndYear(
                DefaultShareholder.fromEntity(shareholder),
                year
        );
        interestUpdate.trigger(interestBooking, shareholder, year);
    }

}
