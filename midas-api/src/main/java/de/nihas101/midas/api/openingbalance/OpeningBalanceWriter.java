package de.nihas101.midas.api.openingbalance;

import org.springframework.transaction.annotation.Transactional;

public interface OpeningBalanceWriter {

    @Transactional
    void create(OpeningBalance openingBalance);

    @Transactional
    void update(OpeningBalance openingBalance);
}
