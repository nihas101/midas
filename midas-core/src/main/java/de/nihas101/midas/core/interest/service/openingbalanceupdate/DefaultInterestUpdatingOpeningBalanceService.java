package de.nihas101.midas.core.interest.service.openingbalanceupdate;

import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.dto.Bookings;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.bookings.service.BookingsWriter;
import de.nihas101.midas.core.interest.InterestCalculation;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.repository.InterestRateRepository;
import de.nihas101.midas.core.interest.service.InterestBookingsReader;
import de.nihas101.midas.core.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.core.openingbalance.service.DefaultOpeningBalanceService;
import de.nihas101.midas.core.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.core.shareholders.dto.Shareholder;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.core.shareholders.repository.ShareholdersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultInterestUpdatingOpeningBalanceService implements InterestUpdatingOpeningBalanceService {

    private final OpeningBalanceService delegate;
    private final BookingsWriter bookingsWriter;
    private final InterestBookingsReader bookingsReader;
    private final ShareholdersRepository shareholdersRepository;
    private final InterestRateRepository interestRateRepository;

    @Autowired
    public DefaultInterestUpdatingOpeningBalanceService(
            final DefaultOpeningBalanceService delegate,
            final BookingsService bookingsWriter,
            final InterestBookingsReader bookingsReader,
            final ShareholdersRepository shareholdersRepository,
            final InterestRateRepository interestRateRepository
    ) {
        this.delegate = delegate;
        this.bookingsWriter = bookingsWriter;
        this.bookingsReader = bookingsReader;
        this.shareholdersRepository = shareholdersRepository;
        this.interestRateRepository = interestRateRepository;
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

    // TODO: This logic is duplicated in InterestUpdatingBookingsService, we can extract this into its own class
    private void updateInterest(final OpeningBalance openingBalance) {
        final Year year = openingBalance.getYear();
        final ShareholderEntity shareholder = shareholdersRepository.getReferenceById(openingBalance.getShareholderId());
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
        bookingsWriter.update(interestBooking);
    }
}
