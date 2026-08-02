package de.nihas101.midas.core.lock;

import de.nihas101.midas.core.bookings.entity.Source;
import de.nihas101.midas.core.lock.service.LockReader;
import de.nihas101.midas.core.lock.service.LockedException;
import de.nihas101.midas.core.money.MoneyAmount;
import de.nihas101.midas.core.openingbalance.dto.OpeningBalance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

@ExtendWith(MockitoExtension.class)
class OpeningBalanceLockTest {

    @Mock
    private LockReader lock;

    @Test
    void assertUnlocked_unlocked() {
        final OpeningBalanceLock openingBalanceLock = new OpeningBalanceLock(new ShareholderLock(lock));

        final OpeningBalance booking = new OpeningBalance(
                1,
                2,
                MoneyAmount.ZERO,
                Year.of(2026),
                Source.USER
        );
        Mockito.when(lock.isLocked(2, Year.of(2026)))
                .thenReturn(false);

        // Assertion passes without throwing
        openingBalanceLock.assertUnlocked(booking);
    }

    @Test
    void assertUnlocked_locked() {
        final OpeningBalanceLock openingBalanceLock = new OpeningBalanceLock(new ShareholderLock(lock));

        final OpeningBalance booking = new OpeningBalance(
                1,
                2,
                MoneyAmount.ZERO,
                Year.of(2026),
                Source.USER
        );
        Mockito.when(lock.isLocked(2, Year.of(2026)))
                .thenReturn(true);

        // Assertion passes without throwing
        Assertions.assertThrows(LockedException.class, () -> openingBalanceLock.assertUnlocked(booking));
    }

    @Test
    void assertUnlocked_nullShareholderId() {
        final OpeningBalanceLock openingBalanceLock = new OpeningBalanceLock(new ShareholderLock(lock));

        final OpeningBalance booking = new OpeningBalance(
                1,
                null,
                MoneyAmount.ZERO,
                Year.of(2026),
                Source.USER
        );

        // Assertion passes without throwing
        openingBalanceLock.assertUnlocked(booking);
    }

    @Test
    void assertUnlocked_nullDate() {
        final OpeningBalanceLock openingBalanceLock = new OpeningBalanceLock(new ShareholderLock(lock));

        final OpeningBalance booking = new OpeningBalance(
                1,
                2,
                MoneyAmount.ZERO,
                null,
                Source.USER
        );

        // Assertion passes without throwing
        openingBalanceLock.assertUnlocked(booking);
    }

    @Test
    void assertUnlocked_nullOpeningBalance() {
        final OpeningBalanceLock openingBalanceLock = new OpeningBalanceLock(new ShareholderLock(lock));

        // Assertion passes without throwing
        openingBalanceLock.assertUnlocked(null);
    }
}