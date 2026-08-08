package de.nihas101.midas.core.shareholders.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class DefaultShareholders implements Shareholders {

    private final List<Shareholder> shareholders;

    @Override
    public List<Shareholder> toList() {
        if (shareholders == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(shareholders);
    }
}
