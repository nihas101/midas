package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

@RequiredArgsConstructor
public class OpeningRunningTotalAccountStatement implements RunningTotalAccountStatement {

    private final OpeningBalance openingBalance;

    @Override
    public MoneyAmount currentBalance() {
        return openingBalance.getOpeningBalance();
    }

    @Override
    public Integer id() {
        return 0; // TODO: Openingbalance needs an id!
    }

    @Override
    public Year year() {
        return openingBalance.getYear();
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        return "Saldovortrag"; // TODO: i18n
    }

    @Override
    public MoneyAmount amount() {
        return openingBalance.getOpeningBalance();
    }
}
