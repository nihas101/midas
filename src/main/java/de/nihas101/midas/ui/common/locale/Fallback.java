package de.nihas101.midas.ui.common.locale;

import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
public class Fallback implements MidasLocaleResolver {

    private final MidasLocaleResolver primary;
    private final MidasLocaleResolver fallback;

    public Locale resolve() {
        return Optional.ofNullable(primary)
                .map(MidasLocaleResolver::resolve)
                .orElseGet(() -> Optional.ofNullable(fallback)
                        .map(MidasLocaleResolver::resolve)
                        .orElse(null));
    }
}
