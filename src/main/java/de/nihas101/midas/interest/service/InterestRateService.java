package de.nihas101.midas.interest.service;

import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.entity.InterestRateEntity;
import de.nihas101.midas.interest.repository.InterestRateRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class InterestRateService {

    private final InterestRateRepository interestRateRepository;
    private final ShareholdersRepository shareholdersRepository;

    public InterestRate interestRate(final Integer shareholderId, final Year year) {
        ShareholderEntity shareholder = shareholdersRepository.findById(shareholderId)
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));

        return interestRateRepository.findByShareholderAndDate(shareholder, year.atDay(1))
                .map(InterestRate::fromEntity)
                .orElse(null);
    }

    public void create(final InterestRate interestRate) {
        if (interestRate.getId() != null) {
            throw new IllegalArgumentException("InterestRateService#create with interestRate.getId() != null");
        }

        upsert(interestRate);
    }

    public void update(final InterestRate interestRate) {
        if (interestRate.getId() == null) {
            throw new IllegalArgumentException("InterestRateService#update with interestRate.getId() == null");
        }

        upsert(interestRate);
    }

    private void upsert(final InterestRate interestRate) {
        ShareholderEntity shareholder = shareholdersRepository.findById(interestRate.getShareholderId())
                .orElseThrow(() -> new IllegalArgumentException("Shareholder not found"));
        interestRateRepository.save(InterestRateEntity.fromDto(interestRate, shareholder));
    }
}
