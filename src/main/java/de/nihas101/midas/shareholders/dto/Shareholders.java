package de.nihas101.midas.shareholders.dto;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Shareholders {
    private final List<Shareholder> shareholders;

    public List<Shareholder> toList() {
        if (shareholders == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(shareholders);
    }
}
