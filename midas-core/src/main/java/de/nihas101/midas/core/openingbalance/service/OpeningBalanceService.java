package de.nihas101.midas.core.openingbalance.service;

import de.nihas101.midas.core.openingbalance.dto.OpeningBalance;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

// TODO: Split into reader and writer
public interface OpeningBalanceService {

    @Transactional
    OpeningBalance openingBalance(final Integer shareholderId, final Year year);

    @Transactional
    void create(OpeningBalance openingBalance);

    @Transactional
    void update(OpeningBalance openingBalance);
}
