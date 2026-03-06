package de.nihas101.midas.shareholders.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Shareholders {
    private final List<Shareholder> shareholders; // TODO: Don't leak this
}
