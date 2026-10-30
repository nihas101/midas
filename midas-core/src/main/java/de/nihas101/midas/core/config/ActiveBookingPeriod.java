package de.nihas101.midas.core.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ActiveBookingPeriod {
    private final CleanupConfig config;

    public LocalDate cleanupStart() {
        return LocalDate.now().minus(config.getCutoff());
    }

    public LocalDate start() {
        return LocalDate.now()
                .minus(config.getCutoff())
                .plusYears(1)
                .withDayOfMonth(1)
                .withMonth(1);
    }

    public LocalDate end() {
        return LocalDate.now()
                .plusYears(1)
                .withDayOfMonth(31)
                .withMonth(12);
    }
}