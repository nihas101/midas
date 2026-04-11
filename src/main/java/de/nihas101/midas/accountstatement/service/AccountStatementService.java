package de.nihas101.midas.accountstatement.service;

import de.nihas101.midas.accountstatement.dto.AccountStatements;
import de.nihas101.midas.accountstatement.dto.DefaultAccountStatements;
import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.accountstatement.repository.AccountStatementsRepository;
import de.nihas101.midas.accountstatement.runningtotal.DefaultRunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.runningtotal.OpeningRunningTotalAccountStatement;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AccountStatementService {

    private static final List<BookingType> TYPE_ORDER = Arrays.stream(BookingType.values())
            .sorted(Comparator.comparingInt(BookingType::getSortKey))
            .toList();
    private final AccountStatementsRepository accountStatementsRepository;
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

        final OpeningBalance openingBalance = openingBalanceRepository.findByShareholderAndDate(
                        ShareholderEntity.fromDto(shareholder),
                        year.atDay(1)
                )
                .map(OpeningBalance::fromEntity)
                .orElse(null);

        return new DefaultAccountStatements(
                accountStatementEntities,
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
}
