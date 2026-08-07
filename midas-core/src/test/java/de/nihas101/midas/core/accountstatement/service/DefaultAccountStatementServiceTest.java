package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.api.accountstatement.AccountStatementRow;
import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatement;
import de.nihas101.midas.core.accountstatement.row.RunningTotalAccountStatementRow;
import de.nihas101.midas.core.accountstatement.runningtotal.DefaultRunningTotalAccountStatement;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.persistance.accountstatements.AccountStatementEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverrideEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverridesRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementsRepository;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceEntity;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class DefaultAccountStatementServiceTest {

    @Mock
    private AccountStatementsRepository accountStatementsRepository;

    @Mock
    private AccountStatementOverridesRepository accountStatementOverridesRepository;

    @Mock
    private OpeningBalanceRepository openingBalanceRepository;

    @Mock
    private AccountStatementOrdersRepository accountStatementOrdersRepository;

    @InjectMocks
    private DefaultAccountStatementService defaultAccountStatementService;

    @Test
    void accountStatements() {
        final Shareholder shareholder = new DefaultShareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);
        final AccountStatementEntity withdrawal = new AccountStatementEntity(
                1,
                LocalDate.of(2026, 1, 2),
                BookingType.WITHDRAWAL,
                MoneyAmount.ofCents(100L)
        );
        final AccountStatementEntity interest = new AccountStatementEntity(
                2,
                LocalDate.of(2026, 2, 2),
                BookingType.INTEREST,
                MoneyAmount.ofCents(100L)
        );
        final AccountStatementEntity taxCredit = new AccountStatementEntity(
                3,
                LocalDate.of(2026, 3, 2),
                BookingType.TAX_CREDIT,
                MoneyAmount.ofCents(100L)
        );
        Mockito.when(
                accountStatementsRepository.accountStatements(
                        shareholder.getId(),
                        year.atMonth(Month.JANUARY).atDay(1),
                        year.atMonth(Month.DECEMBER).atEndOfMonth()
                )
        ).thenReturn(
                List.of(
                        withdrawal,
                        interest,
                        taxCredit
                )
        );
        final OpeningBalanceEntity openingBalance = new OpeningBalanceEntity(
                1,
                DefaultShareholder.fromDto(shareholder),
                LocalDate.of(2026, 1, 1),
                MoneyAmount.ofCents(1000L),
                Source.USER
        );
        Mockito.when(
                openingBalanceRepository.findByShareholderAndDate(
                        DefaultShareholder.fromDto(shareholder),
                        year.atDay(1)
                )
        ).thenReturn(Optional.of(openingBalance));

        final MessageSource messageSource = Mockito.mock(MessageSource.class);
        final Locale locale = Locale.ENGLISH;
        final AccountStatements accountStatements = defaultAccountStatementService.accountStatements(
                shareholder,
                year,
                messageSource,
                locale
        );

        Assertions.assertEquals(DefaultOpeningBalance.fromEntity(openingBalance), accountStatements.openingBalance());
        Assertions.assertEquals(List.of(), accountStatements.manualStatements());
        Assertions.assertEquals(
                new DefaultAccountStatement(withdrawal, messageSource, locale),
                accountStatements.forType(BookingType.WITHDRAWAL)
        );
        Assertions.assertEquals(
                new DefaultAccountStatement(
                        null,
                        Year.of(2026),
                        BookingType.TAX_PREVIOUS_YEAR,
                        MoneyAmount.ofCents(0L),
                        null,
                        true,
                        Source.SYSTEM
                ),
                accountStatements.forType(BookingType.TAX_PREVIOUS_YEAR)
        );
        Assertions.assertEquals(
                new DefaultAccountStatement(taxCredit, messageSource, locale),
                accountStatements.forType(BookingType.TAX_CREDIT)
        );
        Assertions.assertEquals(
                new DefaultAccountStatement(interest, messageSource, locale),
                accountStatements.forType(BookingType.INTEREST)
        );
        Assertions.assertEquals(
                new DefaultAccountStatement(
                        null,
                        Year.of(2026),
                        BookingType.COMPENSATION,
                        MoneyAmount.ofCents(0L),
                        null,
                        true,
                        Source.SYSTEM
                ),
                accountStatements.forType(BookingType.COMPENSATION)
        );
    }

    @Test
    void createOverride() {
        final Shareholder shareholder = new DefaultShareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);
        final BookingType withdrawal = BookingType.WITHDRAWAL;
        Mockito.when(
                accountStatementOverridesRepository
                        .findByShareholderIdAndYearAndBookingType(shareholder.getId(), year.getValue(), withdrawal)
        ).thenReturn(Optional.empty());

        defaultAccountStatementService.saveOverride(
                shareholder,
                year,
                withdrawal,
                MoneyAmount.ofCents(1000L),
                true
        );

        final AccountStatementOverrideEntity expected = new AccountStatementOverrideEntity(
                null,
                DefaultShareholder.fromDto(shareholder),
                year.getValue(),
                withdrawal,
                null,
                true,
                MoneyAmount.ofCents(1000L)
        );
        Mockito.verify(
                accountStatementOverridesRepository,
                Mockito.times(1)
        ).save(Mockito.eq(expected));
    }

    @Test
    void updateOverride() {
        final Shareholder shareholder = new DefaultShareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);
        final BookingType withdrawal = BookingType.WITHDRAWAL;
        Mockito.when(
                accountStatementOverridesRepository
                        .findByShareholderIdAndYearAndBookingType(shareholder.getId(), year.getValue(), withdrawal)
        ).thenReturn(
                Optional.of(
                        new AccountStatementOverrideEntity(
                                12,
                                DefaultShareholder.fromDto(shareholder),
                                2026,
                                BookingType.WITHDRAWAL,
                                "label",
                                true,
                                MoneyAmount.ZERO
                        )
                )
        );

        defaultAccountStatementService.saveOverride(
                shareholder,
                year,
                withdrawal,
                MoneyAmount.ofCents(1000L),
                false
        );

        Mockito.verify(
                accountStatementOverridesRepository,
                Mockito.times(1)
        ).save(Mockito.eq(
                new AccountStatementOverrideEntity(
                        12,
                        DefaultShareholder.fromDto(shareholder),
                        year.getValue(),
                        withdrawal,
                        "label",
                        false,
                        MoneyAmount.ofCents(1000L)
                ))
        );
    }

    @Test
    void saveManualExtra() {
        final Shareholder shareholder = new DefaultShareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);

        final MoneyAmount amount = MoneyAmount.ofCents(1234L);
        defaultAccountStatementService.saveManualExtra(null, shareholder, year, "some-label", amount);

        Mockito.verify(accountStatementOverridesRepository, Mockito.times(1))
                .save(Mockito.eq(
                        AccountStatementOverrideEntity.builder()
                                .id(null)
                                .shareholder(DefaultShareholder.fromDto(shareholder))
                                .year(year.getValue())
                                .bookingType(null)
                                .labelOverride("some-label")
                                .hidden(false)
                                .amount(MoneyAmount.ofCents(1234L))
                                .build()
                ));
    }

    @Test
    void updateManualExtra() {
        final Shareholder shareholder = new DefaultShareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);

        Mockito.when(accountStatementOverridesRepository.findById(1))
                .thenReturn(Optional.of(
                        new AccountStatementOverrideEntity(
                                1,
                                DefaultShareholder.fromDto(shareholder),
                                2026,
                                null,
                                "some-label",
                                false,
                                MoneyAmount.ofCents(1234L)
                        )
                ));

        final MoneyAmount amount = MoneyAmount.ofCents(4321L);
        defaultAccountStatementService.saveManualExtra(1, null, null, "overriden-label", amount);

        Mockito.verify(accountStatementOverridesRepository, Mockito.times(1))
                .save(Mockito.eq(
                        AccountStatementOverrideEntity.builder()
                                .id(1)
                                .shareholder(DefaultShareholder.fromDto(shareholder))
                                .year(year.getValue())
                                .bookingType(null)
                                .labelOverride("overriden-label")
                                .hidden(false)
                                .amount(MoneyAmount.ofCents(4321L))
                                .build()
                ));
    }

    @Test
    void deleteOverride() {
        defaultAccountStatementService.deleteOverride(1);
        Mockito.verify(accountStatementOverridesRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void saveOrder() {
        final Shareholder shareholder = new DefaultShareholder(1, 1, "first", "last");
        final Year year = Year.of(2026);

        final List<String> rowKeys = Stream.of(
                        runningTotalStatement(1, BookingType.WITHDRAWAL, 1L),
                        runningTotalStatement(2, BookingType.TAX_PREVIOUS_YEAR, 2L),
                        runningTotalStatement(3, BookingType.TAX_CREDIT, 3L)
                ).filter(row -> !row.isOpeningBalance())
                .map(AccountStatementRow::rowKey)
                .toList();

        defaultAccountStatementService.saveOrder(
                shareholder,
                year,
                rowKeys
        );

        Mockito.verify(accountStatementOrdersRepository, Mockito.times(1))
                .deleteByShareholderIdAndYear(shareholder.getId(), 2026);
        Mockito.verify(accountStatementOrdersRepository, Mockito.times(rowKeys.size()))
                .save(Mockito.any());
    }

    private RunningTotalAccountStatementRow runningTotalStatement(
            final int id,
            final BookingType taxCredit,
            final long cents
    ) {
        return new RunningTotalAccountStatementRow(
                new DefaultRunningTotalAccountStatement(
                        new DefaultAccountStatement(
                                id,
                                Year.of(2026),
                                taxCredit,
                                MoneyAmount.ofCents(cents),
                                "label"
                        ),
                        MoneyAmount.ZERO
                )
        );
    }
}