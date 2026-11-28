package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.api.interest.InterestRateService;
import de.nihas101.midas.core.lock.ShareholderLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@Primary
@RequiredArgsConstructor
public class LockingInterestRateService implements InterestRateService {

    private final ShareholderLock shareholderLock;
    private final InterestBookingUpdatingInterestRateService delegate;

    @Override
    public InterestRate interestRate(final Integer shareholderId, final Year year) {
        return delegate.interestRate(shareholderId, year);
    }

    @Override
    public InterestRate create(final InterestRate interestRate) {
        assertUnlocked(interestRate);
        return delegate.create(interestRate);
    }

    @Override
    public InterestRate update(final InterestRate interestRate) {
        assertUnlocked(interestRate);
        return delegate.update(interestRate);
    }

    private void assertUnlocked(final InterestRate interestRate) {
        if (interestRate != null) {
            shareholderLock.assertUnlocked(interestRate.getShareholderId(), interestRate.getYear());
        }
    }
}
