package de.nihas101.midas.api.openingbalance;

import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;

import java.time.Year;

public interface OpeningBalanceFactory {
    OpeningBalance create(final MoneyAmount moneyAmount);

    OpeningBalance create(
            final Integer id,
            final MoneyAmount openingBalance,
            final Year year,
            final Source source
    );

    OpeningBalance create(
            final Integer id,
            final Integer shareholderId,
            final MoneyAmount openingBalance,
            final Year year,
            final Source source
    );
}
