package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Locale;

public interface AccountStatementService {
    AccountStatements accountStatements(
            final Shareholder shareholder,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    );

    @Transactional
    void saveExclusion(
            final Shareholder shareholder,
            final Year year,
            final BookingType bookingType,
            final boolean hidden
    );

    @Transactional
    void saveManualExtra(
            final Integer id,
            final Shareholder shareholder,
            final Year year,
            final String label,
            final MoneyAmount amount
    );

    @Transactional
    void deleteOverride(
            final Shareholder shareholder,
            final Year year,
            final Integer id
    );

    @Transactional
    void saveOrder(
            final Shareholder shareholder,
            final Year year,
            final List<String> rowKeys
    );
}
