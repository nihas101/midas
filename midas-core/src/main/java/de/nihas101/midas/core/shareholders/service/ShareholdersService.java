package de.nihas101.midas.core.shareholders.service;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.api.shareholder.ShareholdersWriter;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholders;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.core.shareholders.repository.ShareholdersRepository;
import org.springframework.stereotype.Service;

@Service
public class ShareholdersService implements ShareholdersReader, ShareholdersWriter {

    private final ShareholdersRepository repository;

    public ShareholdersService(final ShareholdersRepository repository) {
        this.repository = repository;
    }

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
        repository.save(ShareholderEntity.fromDto(shareholder));
    }

    @Override
    public void update(final Shareholder shareholder) {
        if (shareholder == null) {
            throw new IllegalArgumentException("ShareholdersService#update with shareholder == null");
        }
        if (shareholder.getId() == null) {
            throw new IllegalArgumentException("ShareholdersService#update with shareholder.getId() == null");
        }
        repository.save(ShareholderEntity.fromDto(shareholder));
    }

    @Override
    public void delete(final Shareholder shareholder) {
        if (shareholder == null) {
            throw new IllegalArgumentException("ShareholdersService#delete with shareholder == null");
        }
        repository.delete(ShareholderEntity.fromDto(shareholder));
    }
}
