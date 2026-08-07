package de.nihas101.midas.vaadin.config;

import lombok.Data;

import java.time.Duration;

@Data
public class DesktopConfig {
    private boolean autoShutdownEnabled = true;
    private Duration gracePeriod = Duration.ofSeconds(60);
    private boolean launchBrowser = true;
}
