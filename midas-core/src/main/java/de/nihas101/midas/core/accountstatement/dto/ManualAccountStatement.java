package de.nihas101.midas.core.accountstatement.dto;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.money.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class ManualAccountStatement implements LabeledAccountStatement {
    private final Integer id;
    private final Year year;
    private final MoneyAmount amount;
    private final String label;
    private final boolean hidden;

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public LocalDate date() {
        return year != null ? year.atMonth(Month.DECEMBER).atEndOfMonth() : null;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public MoneyAmount amount() {
        return amount;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isManualExtra() {
        return true;
    }

}
