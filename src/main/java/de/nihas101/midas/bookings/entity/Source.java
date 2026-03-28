package de.nihas101.midas.bookings.entity;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum Source {
    USER("user"),
    SYSTEM("system");

    @Getter
    private final String source;

    Source(final String source) {
        this.source = source;
    }

    public static Source fromString(final String source) {
        final String trimmedSource = StringUtils.trimToNull(source);
        return Arrays.stream(Source.values())
                .filter(s -> s.getSource().equalsIgnoreCase(trimmedSource))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Source: " + source + " (" + trimmedSource + ")"));
    }
}
