package de.nihas101.midas.core.openingbalance.service;

import de.nihas101.midas.api.lock.LockReader;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.openingbalance.OpeningBalanceService;
import de.nihas101.midas.core.lock.service.LockedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class LockingOpeningBalanceService implements OpeningBalanceService {

    private final LockReader lock;
    private final DefaultOpeningBalanceService delegate;

    @Override
    public OpeningBalance openingBalance(final Integer shareholderId, final Year year) {
        return delegate.openingBalance(shareholderId, year);
    }

    @Override
    public OpeningBalance create(final OpeningBalance openingBalance) {
        assertUnlocked(openingBalance);
        return delegate.create(openingBalance);
    }

    @Override
    public OpeningBalance update(final OpeningBalance openingBalance) {
        assertUnlocked(openingBalance);
        return delegate.update(openingBalance);
    }

    private void assertUnlocked(final OpeningBalance openingBalance) {
        if (lock.isLocked(openingBalance.getShareholderId(), openingBalance.getYear())) {
            throw new LockedException("Cannot modify opening balance: year " + openingBalance.getYear() + " is locked for this shareholder.");
        }
    }
}
