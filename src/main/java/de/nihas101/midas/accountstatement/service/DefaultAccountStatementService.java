package de.nihas101.midas.accountstatement.service;

import de.nihas101.midas.accountstatement.dto.AccountStatements;
import de.nihas101.midas.accountstatement.dto.DefaultAccountStatements;
import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementOrderEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementOrdersRepository;
import de.nihas101.midas.accountstatement.repository.AccountStatementOverrideEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementOverridesRepository;
import de.nihas101.midas.accountstatement.repository.AccountStatementsRepository;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAccountStatementService {

    private final AccountStatementsRepository accountStatementsRepository;
    private final AccountStatementOverridesRepository accountStatementOverridesRepository;
    private final OpeningBalanceRepository openingBalanceRepository;
    private final AccountStatementOrdersRepository accountStatementOrdersRepository;

    public AccountStatements accountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final List<AccountStatementEntity> accountStatementEntities = accountStatementsRepository.accountStatements(
                shareholder.getId(),
                year.atMonth(Month.JANUARY).atDay(1),
                year.atMonth(Month.DECEMBER).atEndOfMonth()
        );

        final List<AccountStatementOverrideEntity> overrides = accountStatementOverridesRepository.findByShareholderIdAndYear(
                shareholder.getId(),
                year.getValue()
        );

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(
                        ShareholderEntity.fromDto(shareholder),
                        year.atDay(1)
                )
                .map(OpeningBalance::fromEntity)
                .orElse(null);

        return new DefaultAccountStatements(
                accountStatementEntities,
                overrides,
                year,
                openingBalance,
                messageSource,
                locale
        );
    }

    @Transactional
    public void saveOverride(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final MoneyAmount amount,
            final boolean hidden
    ) {
        final AccountStatementOverrideEntity entity = accountStatementOverrideEntity(
                shareholder,
                year,
                bookingType,
                amount,
                hidden,
                accountStatementOverridesRepository
                        .findByShareholderIdAndYearAndBookingType(
                                shareholder.getId(),
                                year.getValue(),
                                bookingType
                        )
        );
        accountStatementOverridesRepository.save(entity);
    }

    private AccountStatementOverrideEntity accountStatementOverrideEntity(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final MoneyAmount amount,
            final boolean hidden,
            final Optional<AccountStatementOverrideEntity> existing
    ) {
        if (existing.isPresent()) {
            final AccountStatementOverrideEntity entity = existing.get();
            entity.setAmount(amount);
            entity.setHidden(hidden);
            return entity;
        } else {
            return AccountStatementOverrideEntity.builder()
                    .shareholder(ShareholderEntity.fromDto(shareholder))
                    .year(year.getValue())
                    .bookingType(bookingType)
                    .amount(amount)
                    .hidden(hidden)
                    .build();
        }
    }

    @Transactional
    public void saveManualExtra(
            final Integer id,
            final Shareholder shareholder,
            final Year year,
            final String label,
            final MoneyAmount amount
    ) {
        accountStatementOverridesRepository.save(
                updateAccountStatementOverrideEntity(
                        shareholder,
                        year,
                        label,
                        amount,
                        fetchAccountStatementOverrideEntity(id)
                )
        );
    }

    private Optional<AccountStatementOverrideEntity> fetchAccountStatementOverrideEntity(final Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        return accountStatementOverridesRepository.findById(id);
    }

    private AccountStatementOverrideEntity updateAccountStatementOverrideEntity(
            final Shareholder shareholder,
            final Year year,
            final String label,
            final MoneyAmount amount,
            final Optional<AccountStatementOverrideEntity> entity
    ) {
        if (entity.isPresent()) {
            AccountStatementOverrideEntity en = entity.get();
            en.setLabelOverride(label);
            en.setAmount(amount);
            return en;
        } else {
            return AccountStatementOverrideEntity.builder()
                    .shareholder(ShareholderEntity.fromDto(shareholder))
                    .year(year.getValue())
                    .bookingType(null)
                    .labelOverride(label)
                    .amount(amount)
                    .hidden(false)
                    .build();
        }
    }

    @Transactional
    public void deleteOverride(final Integer id) {
        accountStatementOverridesRepository.deleteById(id);
    }

    @Transactional
    public void saveOrder(
            final Shareholder shareholder,
            final Year year,
            final List<String> rowKeys
    ) {
        accountStatementOrdersRepository.deleteByShareholderIdAndYear(shareholder.getId(), year.getValue());
        accountStatementOrdersRepository.flush();

        final ShareholderEntity shareholderEntity = ShareholderEntity.fromDto(shareholder);
        for (int i = 0; i < rowKeys.size(); i++) {
            final AccountStatementOrderEntity entity = AccountStatementOrderEntity.builder()
                    .shareholder(shareholderEntity)
                    .year(year.getValue())
                    .rowKey(rowKeys.get(i))
                    .position(i)
                    .build();
            accountStatementOrdersRepository.save(entity);
        }
    }
}
