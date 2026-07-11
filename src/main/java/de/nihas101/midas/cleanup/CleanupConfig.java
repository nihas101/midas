package de.nihas101.midas.cleanup;

import lombok.Data;

import java.time.Period;

@Data
public class CleanupConfig {
    private boolean enabled = true;
    private Period cutoff = Period.ofYears(10);
    private int limit = 1000;
}
