package de.nihas101.midas.openingbalance.service;

import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.entity.OpeningBalanceEntity;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class OpeningBalanceService { // TODO: Add reading and writing interface
    // TODO: Add @Transactional on services?

    private final OpeningBalanceRepository openingBalanceRepository;
    private final ShareholdersRepository shareholdersRepository;

    public OpeningBalance openingBalance(Integer shareholderId, Year year) {
        ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        return openingBalanceRepository.findByShareholderAndDate(shareholder, year.atDay(1))
                .map(OpeningBalance::fromEntity)
                .orElse(null);
    }

    public void create(final OpeningBalance openingBalance) {
        if (openingBalance.getId() != null) {
            throw new IllegalArgumentException("OpeningBalanceService#create with openingBalance.getId() != null"); // TODO: i18n
        }

        upsertEntity(openingBalance);
    }

    public void update(final OpeningBalance openingBalance) {
        if (openingBalance.getId() == null) {
            throw new IllegalArgumentException("OpeningBalanceService#update with openingBalance.getId() == null"); // TODO: i18n
        }

        upsertEntity(openingBalance);
    }

    private void upsertEntity(final OpeningBalance openingBalance) {
        ShareholderEntity shareholder = shareholdersRepository.findById(openingBalance.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));
        openingBalanceRepository.save(OpeningBalanceEntity.fromDto(openingBalance, shareholder));
    }
}
