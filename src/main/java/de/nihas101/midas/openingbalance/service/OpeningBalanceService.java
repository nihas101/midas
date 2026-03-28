package de.nihas101.midas.openingbalance.service;

import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

// TODO: Split into reader and writer
public interface OpeningBalanceService {
    @Transactional
    OpeningBalance openingBalance(Integer shareholderId, Year year);

    @Transactional
    void create(OpeningBalance openingBalance);

    @Transactional
    void update(OpeningBalance openingBalance);
}
