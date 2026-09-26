package de.nihas101.midas.core.shareholders.service;

import de.nihas101.midas.api.DeleteMode;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.api.shareholder.ShareholdersWriter;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholders;
import de.nihas101.midas.persistance.bookings.BookingsRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareholdersService implements ShareholdersReader, ShareholdersWriter {

    private final ShareholdersRepository repository;
    private final BookingsRepository bookingsRepository;

    @Override
    public Shareholder shareholder(final int shareholderId) {
        return repository.findById(shareholderId)
                .map(DefaultShareholder::fromEntity)
                .orElse(null);
    }

    @Override
    public Shareholders shareholders() {
        return new DefaultShareholders(
                repository.findAll()
                        .stream()
                        .map(DefaultShareholder::fromEntity)
                        .toList()
        );
    }

    @Override
    public void create(final Shareholder shareholder) {
        if (shareholder == null) {
            throw new IllegalArgumentException("ShareholdersService#create with shareholder == null");
        }
        if (shareholder.getId() != null) {
            throw new IllegalArgumentException("ShareholdersService#create with shareholder.getId() != null");
        }
        repository.save(DefaultShareholder.fromDto(shareholder));
    }

    @Override
    public void update(final Shareholder shareholder) {
        if (shareholder == null) {
            throw new IllegalArgumentException("ShareholdersService#update with shareholder == null");
        }
        if (shareholder.getId() == null) {
            throw new IllegalArgumentException("ShareholdersService#update with shareholder.getId() == null");
        }
        repository.save(DefaultShareholder.fromDto(shareholder));
    }

    @Override
    public void delete(final Shareholder shareholder, final DeleteMode deleteMode) {
        if (shareholder == null) {
            throw new IllegalArgumentException("ShareholdersService#delete with shareholder == null");
        }
        final ShareholderEntity entity = DefaultShareholder.fromDto(shareholder);
        if (deleteMode == DeleteMode.RESTRICT && bookingsRepository.existsByShareholder(entity)) {
            throw new IllegalStateException("Shareholder " + shareholder.getId() + " cannot be safely deleted, due to associated bookings");
        }

        repository.delete(entity);
    }
}
