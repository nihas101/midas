package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.api.accountstatement.AccountStatementService;
import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.lock.ShareholderLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class LockingAccountStatementService implements AccountStatementService {

    private final ShareholderLock shareholderLock;
    private final DefaultAccountStatementService delegate;

    @Override
    public AccountStatements accountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        shareholderLock.assertUnlocked(shareholder, year);
        return delegate.accountStatements(shareholder, year, messageSource, locale);
    }

    @Transactional
    @Override
    public void saveExclusion(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final boolean exclude
    ) {
        shareholderLock.assertUnlocked(shareholder, year);
        delegate.saveExclusion(shareholder, year, bookingType, exclude);
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
        shareholderLock.assertUnlocked(shareholder, year);
        delegate.saveManualExtra(id, shareholder, year, label, amount);
    }

    @Transactional
    @Override
    public void deleteOverride(
            final Shareholder shareholder,
            final Year year,
            final Integer id
    ) {
        shareholderLock.assertUnlocked(shareholder, year);
        delegate.deleteOverride(shareholder, year, id);
    }

    @Transactional
    @Override
    public void saveOrder(
            final Shareholder shareholder,
            final Year year,
            final List<String> rowKeys
    ) {
        shareholderLock.assertUnlocked(shareholder, year);
        delegate.saveOrder(shareholder, year, rowKeys);
    }
}
