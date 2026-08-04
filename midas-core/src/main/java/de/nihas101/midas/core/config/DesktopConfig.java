package de.nihas101.midas.core.config;

import lombok.Data;

import java.time.Duration;

@Data
public class DesktopConfig { // TODO: Move to midas-vaadin
    private boolean autoShutdownEnabled = true;
    private Duration gracePeriod = Duration.ofSeconds(60);
    private boolean launchBrowser = true;
}
