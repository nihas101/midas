package de.nihas101.midas.lock;

import de.nihas101.midas.lock.service.LockReader;
import de.nihas101.midas.lock.service.LockedException;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Year;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareholderLock {

    private final LockReader lock;

    public boolean isLocked(final Shareholder shareholder, final Year year) {
        if (shareholder == null) {
            log.warn("ShareholderLock#isLocked called with null shareholder");
            return false;
        }
        if (year == null) {
            log.warn("ShareholderLock#isLocked called with null year");
            return false;
        }
        return lock.isLocked(shareholder, year);
    }

    public void assertUnlocked(final Shareholder shareholder, final Year year) {
        if (shareholder == null) {
            log.warn("ShareholderLock#assertUnlocked called with null shareholder");
            return;
        }
        if (year == null) {
            log.warn("ShareholderLock#assertUnlocked called with null year");
            return;
        }
        assertUnlocked(shareholder.getId(), year);
    }

    public void assertUnlocked(final Integer shareholderId, final Year year) {
        if (shareholderId == null) {
            log.warn("ShareholderLock#assertUnlocked called with null shareholderId");
            return;
        }
        if (year == null) {
            log.warn("ShareholderLock#assertUnlocked called with null year");
            return;
        }
        if (lock.isLocked(shareholderId, year)) {
            throw new LockedException("Cannot modify entity: year " + year + " is locked for this shareholder (" + shareholderId + ").");
        }
    }

}
