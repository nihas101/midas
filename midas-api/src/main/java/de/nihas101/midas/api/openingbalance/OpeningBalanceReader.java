package de.nihas101.midas.api.openingbalance;

import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

public interface OpeningBalanceReader {

    @Transactional
    OpeningBalance openingBalance(final Integer shareholderId, final Year year);
}
