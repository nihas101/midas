package de.nihas101.midas.lock;

import de.nihas101.midas.lock.service.LockReader;
import de.nihas101.midas.lock.service.LockedException;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
class ShareholderLockTest {

    @Mock
    private LockReader lock;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isLocked(final boolean expected) {
        final ShareholderLock shareholderLock = new ShareholderLock(lock);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);
        Mockito.when(lock.isLocked(shareholder, year))
                .thenReturn(expected);

        Assertions.assertEquals(expected, shareholderLock.isLocked(shareholder, year));
    }

    @ParameterizedTest
    @MethodSource("nullValues")
    void isLocked_null(final Shareholder shareholder, final Year year) {
        final ShareholderLock shareholderLock = new ShareholderLock(lock);
        // No NPE is caused
        shareholderLock.isLocked(shareholder, year);
    }

    @Test
    void assertUnlocked_unlocked() {
        final ShareholderLock shareholderLock = new ShareholderLock(lock);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);
        Mockito.when(lock.isLocked(shareholder.getId(), year))
                .thenReturn(false);

        // Assertion passes without throwing
        shareholderLock.assertUnlocked(shareholder, year);
    }

    @Test
    void assertUnlocked_locked() {
        final ShareholderLock shareholderLock = new ShareholderLock(lock);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);
        Mockito.when(lock.isLocked(shareholder.getId(), year))
                .thenReturn(true);

        // Assertion passes without throwing
        Assertions.assertThrows(LockedException.class, () -> shareholderLock.assertUnlocked(shareholder, year));
    }

    @ParameterizedTest
    @MethodSource("nullValues")
    void assertUnlocked_null(final Shareholder shareholder, final Year year) {
        final ShareholderLock shareholderLock = new ShareholderLock(lock);

        // Assertion passes without throwing
        shareholderLock.assertUnlocked(shareholder, year);
    }

    public static Stream<Arguments> nullValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, Year.of(2026)),
                Arguments.of(new Shareholder(), null)
        );
    }

    @ParameterizedTest
    @MethodSource("nullIdValues")
    void assertUnlocked_null(final Integer shareholderId, final Year year) {
        final ShareholderLock shareholderLock = new ShareholderLock(lock);

        // Assertion passes without throwing
        shareholderLock.assertUnlocked(shareholderId, year);
    }

    public static Stream<Arguments> nullIdValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, Year.of(2026)),
                Arguments.of(1, null)
        );
    }
}