package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
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

    @Override
    public MoneyAmount currentBalance() {
        return openingBalance.getOpeningBalance();
    }

    @Override
    public Integer id() {
        return null;
    }

    @Override
    public LocalDate date() {
        return openingBalance.getYear().atMonth(Month.JANUARY).atDay(1);
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
