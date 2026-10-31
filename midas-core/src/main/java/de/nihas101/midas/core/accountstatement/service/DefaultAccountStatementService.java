package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.api.accountstatement.AccountStatement;
import de.nihas101.midas.api.accountstatement.AccountStatementService;
import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatement;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatementOverride;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatements;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrderEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverride;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverrideEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverridesRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementsRepository;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
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
public class DefaultAccountStatementService implements AccountStatementService {

    private final AccountStatementsRepository accountStatementsRepository;
    private final AccountStatementOverridesRepository accountStatementOverridesRepository;
    private final OpeningBalanceRepository openingBalanceRepository;
    private final AccountStatementOrdersRepository accountStatementOrdersRepository;

    @Override
    public AccountStatements accountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final List<? extends AccountStatement> accountStatements = accountStatementsRepository.accountStatements(
                        shareholder.getId(),
                        year.atMonth(Month.JANUARY).atDay(1),
                        year.atMonth(Month.DECEMBER).atEndOfMonth()
                )
                .stream()
                .map(accountStatement ->
                        DefaultAccountStatement.fromEntity(
                                accountStatement,
                                messageSource,
                                locale
                        )
                ).toList();

        final List<AccountStatementOverride> overrides = accountStatementOverridesRepository.findByShareholderIdAndYear(
                        shareholder.getId(),
                        year.getValue()
                )
                .stream()
                .map(DefaultAccountStatementOverride::fromEntity)
                .toList();

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(
                        DefaultShareholder.fromDto(shareholder),
                        year.atDay(1)
                )
                .map(DefaultOpeningBalance::fromEntity)
                .orElse(null);

        return new DefaultAccountStatements(
                accountStatements,
                overrides,
                year,
                openingBalance,
                messageSource,
                locale
        );
    }

    @Transactional
    @Override
    public void saveExclusion(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final boolean exclude
    ) {
        accountStatementOverridesRepository.save(
                entity(
                        shareholder,
                        year,
                        bookingType,
                        exclude
                )
        );
    }

    private AccountStatementOverrideEntity entity(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final boolean exclude
    ) {
        final AccountStatementOverrideEntity existing = accountStatementOverridesRepository.findByShareholderIdAndYearAndBookingType(
                shareholder.getId(),
                year.getValue(),
                bookingType
        ).orElse(null);
        if (existing != null) {
            existing.setHidden(exclude);
            return existing;
        }

        return AccountStatementOverrideEntity.builder()
                .shareholder(DefaultShareholder.fromDto(shareholder))
                .year(year.getValue())
                .bookingType(bookingType)
                .hidden(exclude)
                .build();

    }

    @Transactional
    @Override
    public void saveManualExtra(
            final Integer id,
            final Shareholder shareholder,
            final Year year,
            final String label,
            final MoneyAmount amount
    ) {
        accountStatementOverridesRepository.save(
                getManualExtra(
                        id,
                        shareholder,
                        year,
                        label,
                        amount
                )
        );
    }

    private AccountStatementOverrideEntity getManualExtra(
            final Integer id,
            final Shareholder shareholder,
            final Year year,
            final String label,
            final MoneyAmount amount
    ) {
        final AccountStatementOverrideEntity entity = fetchAccountStatementOverrideEntity(id)
                .orElse(null);
        if (entity != null) {
            entity.setLabelOverride(label);
            entity.setAmount(amount);
            return entity;
        }

        return AccountStatementOverrideEntity.builder()
                .shareholder(DefaultShareholder.fromDto(shareholder))
                .year(year.getValue())
                .bookingType(null)
                .labelOverride(label)
                .amount(amount)
                .hidden(false)
                .build();
    }

    private Optional<AccountStatementOverrideEntity> fetchAccountStatementOverrideEntity(final Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        return accountStatementOverridesRepository.findById(id);
    }

    @Transactional
    @Override
    public void deleteOverride(
            final Shareholder shareholder,
            final Year year,
            final Integer id
    ) {
        accountStatementOverridesRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void saveOrder(
            final Shareholder shareholder,
            final Year year,
            final List<String> rowKeys
    ) {
        accountStatementOrdersRepository.deleteByShareholderIdAndYear(shareholder.getId(), year.getValue());

        final ShareholderEntity shareholderEntity = DefaultShareholder.fromDto(shareholder);
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
