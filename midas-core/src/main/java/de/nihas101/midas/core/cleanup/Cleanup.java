package de.nihas101.midas.core.cleanup;

import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverridesRepository;
import de.nihas101.midas.persistance.bookings.BookingsRepository;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.lock.LockRepository;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class Cleanup {

    private final BookingsRepository bookingsRepository;
    private final LockRepository lockRepository;
    private final AccountStatementOrdersRepository accountStatementOrdersRepository;
    private final AccountStatementOverridesRepository accountStatementOverridesRepository;
    private final InterestRateRepository interestRateRepository;
    private final OpeningBalanceRepository openingBalanceRepository;
    private final CleanupConfig cleanupConfig;

    @Autowired
    public Cleanup(
            final BookingsRepository bookingsRepository,
            final LockRepository lockRepository,
            final AccountStatementOrdersRepository accountStatementOrdersRepository,
            final AccountStatementOverridesRepository accountStatementOverridesRepository,
            final InterestRateRepository interestRateRepository,
            final OpeningBalanceRepository openingBalanceRepository,
            final CoreConfig midasConfig
    ) {
        this(
                bookingsRepository,
                lockRepository,
                accountStatementOrdersRepository,
                accountStatementOverridesRepository,
                interestRateRepository,
                openingBalanceRepository,
                midasConfig.getCleanup()
        );
    }

    public void cleanUp() {
        final LocalDate cutoff = LocalDate.now().minus(cleanupConfig.getCutoff());
        final int limit = cleanupConfig.getLimit();

        final int deletedBookingsCount = deletedBookings(limit, cutoff);
        log.info("Removed {} bookings (limit: {}), because they were made before the cutoff ({})",
                deletedBookingsCount, limit, cutoff);

        final LocalDate orphanCutoffDate = cutoff.minusYears(1);
        final int deletedLocksCount = lockRepository.deleteOrphanedLocks(orphanCutoffDate.getYear());
        log.info("Removed {} orphaned year locks", deletedLocksCount);

        final int deletedStatementOrders = accountStatementOrdersRepository.deleteOrphanedStatementOrders(orphanCutoffDate.getYear());
        log.info("Removed {} orphaned statement orders", deletedStatementOrders);

        final int deletedStatementOverrides = accountStatementOverridesRepository.deleteOrphanedStatementOverrides(orphanCutoffDate.getYear());
        log.info("Removed {} orphaned statement overrides", deletedStatementOverrides);

        final int deletedInterestRates = interestRateRepository.deleteOrphanedInterestRates(orphanCutoffDate);
        log.info("Removed {} orphaned interest rates", deletedInterestRates);

        final int deletedOpeningBalances = openingBalanceRepository.deleteOrphanedOpeningBalances(orphanCutoffDate);
        log.info("Removed {} orphaned opening balances", deletedOpeningBalances);
    }

    private int deletedBookings(final int limit, final LocalDate cutoff) {
        return limit > -1
                ? bookingsRepository.deleteBeforeWithLimit(cutoff, limit)
                : bookingsRepository.deleteBefore(cutoff);
    }
}
