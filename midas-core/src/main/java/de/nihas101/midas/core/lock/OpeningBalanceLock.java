package de.nihas101.midas.core.lock;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpeningBalanceLock {

    private final ShareholderLock delegate;

    public void assertUnlocked(final OpeningBalance openingBalance) {
        delegate.assertUnlocked(
                openingBalance != null ? openingBalance.getShareholderId() : null,
                openingBalance != null ? openingBalance.getYear() : null
        );
    }
}
