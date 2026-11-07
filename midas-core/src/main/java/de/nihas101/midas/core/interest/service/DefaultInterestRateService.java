package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.api.interest.InterestRateService;
import de.nihas101.midas.core.interest.dto.DefaultInterestRate;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class DefaultInterestRateService implements InterestRateService {

    private final InterestRateRepository interestRateRepository;
    private final ShareholdersRepository shareholdersRepository;

    @Override
    public InterestRate interestRate(final Integer shareholderId, final Year year) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        return interestRateRepository.findByShareholderAndDate(shareholder, year.atDay(1))
                .map(DefaultInterestRate::fromEntity)
                .orElse(null);
    }

    @Override
    public InterestRate create(final InterestRate interestRate) {
        if (interestRate.getId() != null) {
            throw new IllegalArgumentException("InterestRateService#create with interestRate.getId() != null");
        }

        // TODO: Check if an interest rate already exists

        return upsert(interestRate);
    }

    @Override
    public InterestRate update(final InterestRate interestRate) {
        if (interestRate.getId() == null) {
            throw new IllegalArgumentException("InterestRateService#update with interestRate.getId() == null");
        }

        return upsert(interestRate);
    }

    private InterestRate upsert(InterestRate interestRate) {
        final ShareholderEntity shareholder = shareholdersRepository.findById(interestRate.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));
        return DefaultInterestRate.fromEntity(
                interestRateRepository.save(
                        DefaultInterestRate.fromDto(
                                interestRate,
                                shareholder
                        )
                )
        );
    }
}
