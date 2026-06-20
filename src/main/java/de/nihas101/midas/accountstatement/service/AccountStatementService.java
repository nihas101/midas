package de.nihas101.midas.accountstatement.service;

import de.nihas101.midas.accountstatement.dto.AccountStatements;
import de.nihas101.midas.accountstatement.dto.DefaultAccountStatements;
import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementOverrideEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementOverridesRepository;
import de.nihas101.midas.accountstatement.repository.AccountStatementsRepository;
import de.nihas101.midas.accountstatement.runningtotal.DefaultRunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.runningtotal.OpeningRunningTotalAccountStatement;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountStatementService {

    private static final List<BookingType> TYPE_ORDER = Arrays.stream(BookingType.values())
            .sorted(Comparator.comparingInt(BookingType::getSortKey))
            .toList();
    private final AccountStatementsRepository accountStatementsRepository;
    private final AccountStatementOverridesRepository accountStatementOverridesRepository;
    private final OpeningBalanceRepository openingBalanceRepository;

    public AccountStatements accountStatements(
            final Shareholder shareholder,
            final Year year,
            // TODO: Inject the message source and locale (via a class similar to MidasLocaleResolver) rather than passing it in like this
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

    public RunningTotalAccountStatements runningTotalAccountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final AccountStatements accountStatements = this.accountStatements(
                shareholder,
                year,
                messageSource,
                locale
        );
        return new DefaultRunningTotalAccountStatements(
                accountStatements,
                TYPE_ORDER,
                new OpeningRunningTotalAccountStatement(
                        accountStatements.openingBalance(),
                        messageSource,
                        locale
                )
        );
    }

    // TODO: Maybe just combine with the other method?
    @Transactional
    public void setHidden(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final boolean hidden
    ) {
        final Optional<AccountStatementOverrideEntity> existing = accountStatementOverridesRepository
                .findByShareholderIdAndYearAndBookingType(shareholder.getId(), year.getValue(), bookingType);

        final AccountStatementOverrideEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setHidden(hidden);
        } else {
            entity = AccountStatementOverrideEntity.builder()
                    .shareholder(ShareholderEntity.fromDto(shareholder))
                    .year(year.getValue())
                    .bookingType(bookingType)
                    .hidden(hidden)
                    .build();
        }
        accountStatementOverridesRepository.save(entity);
    }

    // TODO: Pass in an override rather than the things individually?
    @Transactional
    public void saveOverride(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final MoneyAmount amount
    ) {
        final Optional<AccountStatementOverrideEntity> existing = accountStatementOverridesRepository
                .findByShareholderIdAndYearAndBookingType(shareholder.getId(), year.getValue(), bookingType);

        final AccountStatementOverrideEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setAmount(amount);
        } else {
            entity = AccountStatementOverrideEntity.builder()
                    .shareholder(ShareholderEntity.fromDto(shareholder))
                    .year(year.getValue())
                    .bookingType(bookingType)
                    .amount(amount)
                    .build();
        }
        accountStatementOverridesRepository.save(entity);
    }

    @Transactional
    public void saveManualExtra(
            final Shareholder shareholder,
            final Year year,
            final String label,
            final MoneyAmount amount
    ) {
        final AccountStatementOverrideEntity entity = AccountStatementOverrideEntity.builder()
                .shareholder(ShareholderEntity.fromDto(shareholder))
                .year(year.getValue())
                .bookingType(null)
                .labelOverride(label)
                .amount(amount)
                .build();
        accountStatementOverridesRepository.save(entity);
    }

    @Transactional
    public void updateManualExtra(
            final Integer id,
            final String label,
            final MoneyAmount amount
    ) {
        accountStatementOverridesRepository.findById(id).ifPresent(entity -> {
            entity.setLabelOverride(label);
            entity.setAmount(amount);
            accountStatementOverridesRepository.save(entity);
        });
    }

    @Transactional
    public void deleteOverride(final Integer id) {
        accountStatementOverridesRepository.deleteById(id);
    }
}
