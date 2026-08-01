package de.nihas101.midas.interest.service.openingbalanceupdate;

import de.nihas101.midas.lock.OpeningBalanceLock;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
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
