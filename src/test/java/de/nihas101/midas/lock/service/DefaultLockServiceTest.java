package de.nihas101.midas.lock.service;

import de.nihas101.midas.lock.entity.LockEntity;
import de.nihas101.midas.lock.repository.LockRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DefaultLockServiceTest {

    @Mock
    private LockRepository lockRepository;

    @Mock
    private ShareholdersRepository shareholdersRepository;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isLocked_true(boolean expected) {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final int shareholderId = 1;
        final ShareholderEntity shareholderEntity = new ShareholderEntity(shareholderId, shareholderId, "first", "last");
        Mockito.when(shareholdersRepository.findById(shareholderId))
                .thenReturn(Optional.of(shareholderEntity));

        final Year year = Year.of(2026);
        Mockito.when(lockRepository.existsByShareholderAndYear(shareholderEntity, year.getValue()))
                .thenReturn(expected);

        final boolean locked = lockService.isLocked(shareholderId, year);
        Assertions.assertEquals(expected, locked);
    }

    @Test
    void isLocked_unknownShareholder() {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final int shareholderId = 1;
        Mockito.when(shareholdersRepository.findById(shareholderId))
                .thenReturn(Optional.empty());

        final Year year = Year.of(2026);
        Assertions.assertThrows(IllegalArgumentException.class, () -> lockService.isLocked(shareholderId, year));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isLocked_shareholder(final boolean expected) {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final Year year = Year.of(2026);
        Mockito.when(lockRepository.existsByShareholderAndYear(any(), Mockito.eq(year.getValue())))
                .thenReturn(expected);

        final boolean locked = lockService.isLocked(new Shareholder(1, 1, "first", "last"), year);
        Assertions.assertEquals(expected, locked);
    }

    @Test
    void lock_unlocked() {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final ShareholderEntity shareholderEntity = ShareholderEntity.fromDto(shareholder);
        final Year year = Year.of(2026);
        Mockito.when(lockRepository.existsByShareholderAndYear(shareholderEntity, year.getValue()))
                .thenReturn(false);

        lockService.lock(shareholder, year);

        Mockito.verify(lockRepository, Mockito.times(1))
                .save(new LockEntity(null, shareholderEntity, year.getValue()));
    }

    @Test
    void lock_alreadyLocked() {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final ShareholderEntity shareholderEntity = ShareholderEntity.fromDto(shareholder);
        final Year year = Year.of(2026);
        Mockito.when(lockRepository.existsByShareholderAndYear(shareholderEntity, year.getValue()))
                .thenReturn(true);

        lockService.lock(shareholder, year);

        Mockito.verify(lockRepository, Mockito.never()).save(any());
    }

    @Test
    void unlock_notLocked() {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final ShareholderEntity shareholderEntity = ShareholderEntity.fromDto(shareholder);
        final Year year = Year.of(2026);
        Mockito.when(lockRepository.findByShareholderAndYear(shareholderEntity, year.getValue()))
                .thenReturn(Optional.empty());

        lockService.unlock(shareholder, year);

        Mockito.verify(lockRepository, Mockito.never())
                .delete(any());
    }

    @Test
    void unlock_locked() {
        final LockService lockService = new DefaultLockService(lockRepository, shareholdersRepository);

        final Shareholder shareholder = new Shareholder(1, 1, "first", "last");
        final ShareholderEntity shareholderEntity = ShareholderEntity.fromDto(shareholder);
        final Year year = Year.of(2026);
        final LockEntity lockEntity = new LockEntity(1, shareholderEntity, year.getValue());
        Mockito.when(lockRepository.findByShareholderAndYear(shareholderEntity, year.getValue()))
                .thenReturn(Optional.of(lockEntity));

        lockService.unlock(shareholder, year);

        Mockito.verify(lockRepository, Mockito.times(1))
                .delete(lockEntity);
    }
}