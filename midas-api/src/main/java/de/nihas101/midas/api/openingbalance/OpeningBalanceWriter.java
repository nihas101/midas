package de.nihas101.midas.api.openingbalance;

import org.springframework.transaction.annotation.Transactional;

public interface OpeningBalanceWriter {

    @Transactional
    OpeningBalance create(OpeningBalance openingBalance);

    @Transactional
    OpeningBalance update(OpeningBalance openingBalance);
}
