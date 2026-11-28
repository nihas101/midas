package de.nihas101.midas.api.interest;

import java.time.Year;

public interface InterestRateService {
    InterestRate interestRate(final Integer shareholderId, final Year year);

    InterestRate create(final InterestRate interestRate);

    InterestRate update(final InterestRate interestRate);

}
