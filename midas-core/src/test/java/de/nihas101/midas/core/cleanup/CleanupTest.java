package de.nihas101.midas.core.cleanup;

import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.config.DatesConfig;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ShareholderConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.config.TitleConfig;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.persistance.DbConfig;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverridesRepository;
import de.nihas101.midas.persistance.bookings.BookingsRepository;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.lock.LockRepository;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;

@ExtendWith(MockitoExtension.class)
class CleanupTest {

    @Mock
    private BookingsRepository bookingsRepository;

    @Mock
    private LockRepository lockRepository;

    @Mock
    private AccountStatementOrdersRepository accountStatementOrdersRepository;

    @Mock
    private AccountStatementOverridesRepository accountStatementOverridesRepository;

    @Mock
    private InterestRateRepository interestRateRepository;

    @Mock
    private OpeningBalanceRepository openingBalanceRepository;

    @Test
    void defaultCleanUp() {
        final CoreConfig config = createConfig();

        final Cleanup cleanup = new Cleanup(
                bookingsRepository,
                lockRepository,
                accountStatementOrdersRepository,
                accountStatementOverridesRepository,
                interestRateRepository,
                openingBalanceRepository,
                config
        );
        cleanup.cleanUp();

        final LocalDate expectedCutoff = LocalDate.now().minusYears(10);
        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                expectedCutoff,
                1000
        );
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                expectedCutoff.minusYears(1).getYear()
        );
    }

    @Test
    void defaultCleanUpFromMidasConfig() {
        final Cleanup cleanup = new Cleanup(
                bookingsRepository,
                lockRepository,
                accountStatementOrdersRepository,
                accountStatementOverridesRepository,
                interestRateRepository,
                openingBalanceRepository,
                new CoreConfig(
                        new TitleConfig(),
                        new ThemeConfig(),
                        new UIConfig(),
                        new I18nConfig(),
                        new CleanupConfig(),
                        new DbConfig() {
                        },
                        new DatesConfig(),
                        new ShareholderConfig()
                )
        );
        cleanup.cleanUp();

        final LocalDate expectedCutoff = LocalDate.now().minusYears(10);
        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                expectedCutoff,
                1000
        );

        final LocalDate expectedOrphanCutoff = expectedCutoff.minusYears(1);
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(accountStatementOrdersRepository, Mockito.times(1)).deleteOrphanedStatementOrders(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(accountStatementOverridesRepository, Mockito.times(1)).deleteOrphanedStatementOverrides(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(interestRateRepository, Mockito.times(1)).deleteOrphanedInterestRates(
                expectedOrphanCutoff
        );
        Mockito.verify(openingBalanceRepository, Mockito.times(1)).deleteOrphanedOpeningBalances(
                expectedOrphanCutoff
        );
    }

    @Test
    void cleanUpWithCustomCutoff() {
        final CoreConfig config = createConfig();
        final Period cutoff = Period.ofDays(7);
        config.getCleanup().setCutoff(cutoff);

        final Cleanup cleanup = new Cleanup(
                bookingsRepository,
                lockRepository,
                accountStatementOrdersRepository,
                accountStatementOverridesRepository,
                interestRateRepository,
                openingBalanceRepository,
                config
        );
        cleanup.cleanUp();

        final LocalDate expectedCutoff = LocalDate.now().minus(cutoff);
        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBeforeWithLimit(
                expectedCutoff,
                1000
        );

        final LocalDate expectedOrphanCutoff = expectedCutoff.minusYears(1);
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(accountStatementOrdersRepository, Mockito.times(1)).deleteOrphanedStatementOrders(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(accountStatementOverridesRepository, Mockito.times(1)).deleteOrphanedStatementOverrides(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(interestRateRepository, Mockito.times(1)).deleteOrphanedInterestRates(
                expectedOrphanCutoff
        );
        Mockito.verify(openingBalanceRepository, Mockito.times(1)).deleteOrphanedOpeningBalances(
                expectedOrphanCutoff
        );
    }

    @Test
    void cleanUpWithoutLimit() {
        final CoreConfig config = createConfig();
        config.getCleanup().setLimit(-1);

        final Cleanup cleanup = new Cleanup(
                bookingsRepository,
                lockRepository,
                accountStatementOrdersRepository,
                accountStatementOverridesRepository,
                interestRateRepository,
                openingBalanceRepository,
                config
        );
        cleanup.cleanUp();

        final LocalDate expectedCutoff = LocalDate.now().minusYears(10);
        Mockito.verify(bookingsRepository, Mockito.times(1)).deleteBefore(expectedCutoff);

        final LocalDate expectedOrphanCutoff = expectedCutoff.minusYears(1);
        Mockito.verify(lockRepository, Mockito.times(1)).deleteOrphanedLocks(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(accountStatementOrdersRepository, Mockito.times(1)).deleteOrphanedStatementOrders(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(accountStatementOverridesRepository, Mockito.times(1)).deleteOrphanedStatementOverrides(
                expectedOrphanCutoff.getYear()
        );
        Mockito.verify(interestRateRepository, Mockito.times(1)).deleteOrphanedInterestRates(
                expectedOrphanCutoff
        );
        Mockito.verify(openingBalanceRepository, Mockito.times(1)).deleteOrphanedOpeningBalances(
                expectedOrphanCutoff
        );
    }

    private static CoreConfig createConfig() {
        return new CoreConfig(
                new TitleConfig(),
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                new CleanupConfig(),
                new DbConfig() {
                },
                new DatesConfig(),
                new ShareholderConfig()
        );
    }
}