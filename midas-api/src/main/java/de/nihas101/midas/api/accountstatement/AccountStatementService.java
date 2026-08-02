package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.api.bookings.BookingType;
import de.nihas101.midas.api.money.MoneyAmount;
import de.nihas101.midas.api.shareholder.Shareholder;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Locale;

public interface AccountStatementService {
    AccountStatements accountStatements(
            Shareholder shareholder,
            Year year,
            MessageSource messageSource,
            Locale locale
    );

    @Transactional
    void saveOverride(
            Shareholder shareholder,
            Year year,
            BookingType bookingType,
            MoneyAmount amount,
            boolean hidden
    );

    @Transactional
    void saveManualExtra(
            Integer id,
            Shareholder shareholder,
            Year year,
            String label,
            MoneyAmount amount
    );

    @Transactional
    void deleteOverride(Integer id);

    @Transactional
    void saveOrder(
            Shareholder shareholder,
            Year year,
            List<String> rowKeys
    );
}
