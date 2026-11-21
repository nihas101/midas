package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.core.interest.dto.DefaultInterestRate;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.lock.service.LockedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LockingInterestRateServiceTest {

    @Mock
    private ShareholderLock shareholderLock;

    @Mock
    private InterestBookingUpdatingInterestRateService delegate;

    private LockingInterestRateService service;

    @BeforeEach
    void setUp() {
        service = new LockingInterestRateService(shareholderLock, delegate);
    }

    @Test
    void interestRate_delegatesWithoutLockCheck() {
        final Year year = Year.of(2026);
        final InterestRate expected = new DefaultInterestRate(1, 10, new BigDecimal("0.05"), year);
        when(delegate.interestRate(10, year)).thenReturn(expected);

        final InterestRate result = service.interestRate(10, year);

        assertEquals(expected, result);
        verify(shareholderLock, never()).assertUnlocked(10, year);
    }

    @Test
    void create_whenUnlocked_delegates() {
        final Year year = Year.of(2026);
        final InterestRate input = new DefaultInterestRate(null, 10, new BigDecimal("0.05"), year);
        final InterestRate created = new DefaultInterestRate(1, 10, new BigDecimal("0.05"), year);
        doNothing().when(shareholderLock).assertUnlocked(10, year);
        when(delegate.create(input)).thenReturn(created);

        final InterestRate result = service.create(input);

        assertEquals(created, result);
        verify(shareholderLock).assertUnlocked(10, year);
        verify(delegate).create(input);
    }

    @Test
    void create_whenLocked_throwsLockedException() {
        final Year year = Year.of(2026);
        final InterestRate input = new DefaultInterestRate(null, 10, new BigDecimal("0.05"), year);
        doThrow(new LockedException("Locked")).when(shareholderLock).assertUnlocked(10, year);

        assertThrows(LockedException.class, () -> service.create(input));
        verify(delegate, never()).create(input);
    }

    @Test
    void update_whenUnlocked_delegates() {
        final Year year = Year.of(2026);
        final InterestRate input = new DefaultInterestRate(1, 10, new BigDecimal("0.06"), year);
        doNothing().when(shareholderLock).assertUnlocked(10, year);
        when(delegate.update(input)).thenReturn(input);

        final InterestRate result = service.update(input);

        assertEquals(input, result);
        verify(shareholderLock).assertUnlocked(10, year);
        verify(delegate).update(input);
    }

    @Test
    void update_whenLocked_throwsLockedException() {
        final Year year = Year.of(2026);
        final InterestRate input = new DefaultInterestRate(1, 10, new BigDecimal("0.06"), year);
        doThrow(new LockedException("Locked")).when(shareholderLock).assertUnlocked(10, year);

        assertThrows(LockedException.class, () -> service.update(input));
        verify(delegate, never()).update(input);
    }
}
