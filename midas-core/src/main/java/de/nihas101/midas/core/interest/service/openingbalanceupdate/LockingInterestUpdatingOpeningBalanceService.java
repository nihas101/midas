package de.nihas101.midas.core.interest.service.openingbalanceupdate;

import de.nihas101.midas.api.interest.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.core.lock.OpeningBalanceLock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@Primary
@RequiredArgsConstructor
public class LockingInterestUpdatingOpeningBalanceService implements InterestUpdatingOpeningBalanceService {

    private final OpeningBalanceLock lock;
    private final DefaultInterestUpdatingOpeningBalanceService delegate;

    @Override
    public OpeningBalance openingBalance(final Integer shareholderId, final Year year) {
        return delegate.openingBalance(shareholderId, year);
    }

    @Override
    public void create(final OpeningBalance openingBalance) {
        lock.assertUnlocked(openingBalance);
        delegate.create(openingBalance);
    }

    @Override
    public void update(final OpeningBalance openingBalance) {
        lock.assertUnlocked(openingBalance);
        delegate.update(openingBalance);
    }
}
