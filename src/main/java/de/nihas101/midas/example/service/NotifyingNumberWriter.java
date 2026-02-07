package de.nihas101.midas.example.service;

import de.nihas101.midas.ui.main.Dependant;

import java.util.List;

public class NotifyingNumberWriter implements NumberWriter {

    private final NumberWriter delegate;
    private final List<Dependant> dependants;

    public NotifyingNumberWriter(
            NumberWriter delegate,
            List<Dependant> dependants
    ) {
        this.delegate = delegate;
        this.dependants = dependants;
    }

    @Override
    public void addNumber(final Integer value) {
        delegate.addNumber(value);
        dependants.forEach(Dependant::update);
    }
}
