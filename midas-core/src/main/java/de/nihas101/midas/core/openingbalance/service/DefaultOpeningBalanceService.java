package de.nihas101.midas.core.openingbalance.service;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.openingbalance.OpeningBalanceService;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class DefaultOpeningBalanceService implements OpeningBalanceService {

    private final OpeningBalanceRepository openingBalanceRepository;
    private final ShareholdersRepository shareholdersRepository;

    @Override
    public OpeningBalance openingBalance(final Integer shareholderId, final Year year) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        return openingBalanceRepository.findByShareholderAndDate(shareholder, year.atDay(1))
                .map(DefaultOpeningBalance::fromEntity)
                .orElse(null);
    }

    @Override
    public OpeningBalance create(final OpeningBalance openingBalance) {
        if (openingBalance.getId() != null) {
            throw new IllegalArgumentException("OpeningBalanceService#create with openingBalance.getId() != null");
        }

        return upsertEntity(openingBalance);
    }

    @Override
    public OpeningBalance update(final OpeningBalance openingBalance) {
        if (openingBalance.getId() == null) {
            throw new IllegalArgumentException("OpeningBalanceService#update with openingBalance.getId() == null");
        }

        return upsertEntity(openingBalance);
    }

    private OpeningBalance upsertEntity(final OpeningBalance openingBalance) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(openingBalance.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));
        return DefaultOpeningBalance.fromEntity(
                openingBalanceRepository.save(
                        DefaultOpeningBalance.fromDto(openingBalance, shareholder)
                )
        );
    }
}
