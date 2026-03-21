package de.nihas101.midas.config;

import lombok.Data;

@Data
public class DesktopConfig {
    private boolean autoShutdownEnabled = true;
    private int gracePeriodSeconds = 60; // TODO: Define this via PTD string?
}
