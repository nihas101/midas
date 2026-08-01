package de.nihas101.midas.lock.service;

import de.nihas101.midas.lock.entity.LockEntity;
import de.nihas101.midas.lock.repository.LockRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class DefaultLockService implements LockService {

    private final LockRepository lockRepository;
    private final ShareholdersRepository shareholdersRepository;

    @Override
    public boolean isLocked(final Integer shareholderId, final Year year) {
        final Shareholder shareholder = shareholdersRepository.findById(shareholderId)
                .map(Shareholder::fromEntity)
                .orElse(null);
        if (shareholder == null) {
            throw new IllegalArgumentException("No shareholder with id " + shareholderId + " exists");
        }

        return this.isLocked(shareholder, year);
    }

    @Override
    public boolean isLocked(final Shareholder shareholder, final Year year) {
        return lockRepository.existsByShareholderAndYear(ShareholderEntity.fromDto(shareholder), year.getValue());
    }

    @Override
    public void lock(final Shareholder shareholder, final Year year) {
        final ShareholderEntity shareholderEntity = ShareholderEntity.fromDto(shareholder);
        if (lockRepository.existsByShareholderAndYear(shareholderEntity, year.getValue())) {
            return;
        }
        lockRepository.save(new LockEntity(null, shareholderEntity, year.getValue()));
    }

    @Override
    public void unlock(final Shareholder shareholder, final Year year) {
        lockRepository.findByShareholderAndYear(ShareholderEntity.fromDto(shareholder), year.getValue())
                .ifPresent(lockRepository::delete);
    }
}
