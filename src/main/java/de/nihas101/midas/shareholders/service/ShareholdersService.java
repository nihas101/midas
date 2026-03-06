package de.nihas101.midas.shareholders.service;

import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.dto.Shareholders;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import org.springframework.stereotype.Service;

@Service
public class ShareholdersService implements ShareholdersReader, ShareholdersWriter {

    private final ShareholdersRepository repository;

    public ShareholdersService(final ShareholdersRepository repository) {
        this.repository = repository;
    }

    @Override
    public Shareholders shareholders() {
        return new Shareholders(
                repository.findAll()
                        .stream()
                        .map(Shareholder::fromEntity)
                        .toList()
        );
    }

    @Override
    public void create(final Shareholder shareholder) {
        if (shareholder.getId() != null) {
            throw new IllegalArgumentException("ShareholdersService#create with shareholder.getId() != null");
        }
        repository.save(ShareholderEntity.fromDto(shareholder));
    }

    @Override
    public void update(final Shareholder shareholder) {
        if (shareholder.getId() == null) {
            throw new IllegalArgumentException("ShareholdersService#udpate with shareholder.getId() == null");
        }
        repository.save(ShareholderEntity.fromDto(shareholder));
    }

    @Override
    public void delete(final Shareholder shareholder) {
        repository.delete(ShareholderEntity.fromDto(shareholder));
    }
}
