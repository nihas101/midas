package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class OpeningRunningTotalAccountStatement implements RunningTotalAccountStatement {

    private final OpeningBalance openingBalance;
    private final String label;

    public OpeningRunningTotalAccountStatement(
            final OpeningBalance openingBalance,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                openingBalance,
                messageSource.getMessage("account-statement.opening-balance", null, locale)
        );
    }

    @Override
    public MoneyAmount currentBalance() {
        if (openingBalance != null) {
            return openingBalance.getOpeningBalance();
        } else {
            return MoneyAmount.ZERO;
        }
    }

    @Override
    public Integer id() {
        return null;
    }

    @Override
    public LocalDate date() {
        if (openingBalance != null) {
            return openingBalance.getYear().atMonth(Month.JANUARY).atDay(1);
        } else {
            return LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);
        }
    }

    public String label() {
        return label;
    }

    @Override
    public MoneyAmount amount() {
        if (openingBalance != null) {
            return openingBalance.getOpeningBalance();
        } else {
            return MoneyAmount.ZERO;
        }
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isManualExtra() {
        return false;
    }

    @Override
    public BookingType bookingType() {
        return null;
    }
}
