package de.nihas101.midas.core.openingbalance.dto;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.openingbalance.OpeningBalanceFactory;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class DefaultOpeningBalanceFactory implements OpeningBalanceFactory {

    @Override
    public OpeningBalance create(final MoneyAmount moneyAmount) {
        return new DefaultOpeningBalance(moneyAmount);
    }

    @Override
    public OpeningBalance create(
            final Integer shareholderId,
            final MoneyAmount openingBalance,
            final Year year,
            final Source source
    ) {
        return new DefaultOpeningBalance(
                null,
                shareholderId,
                openingBalance,
                year,
                source
        );
    }

    @Override
    public OpeningBalance create(
            final Integer id,
            final Integer shareholderId,
            final MoneyAmount openingBalance,
            final Year year,
            final Source source
    ) {
        return new DefaultOpeningBalance(
                id,
                shareholderId,
                openingBalance,
                year,
                source
        );
    }
}
