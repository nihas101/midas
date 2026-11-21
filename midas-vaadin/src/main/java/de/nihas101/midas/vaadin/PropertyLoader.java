package de.nihas101.midas.vaadin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    public Properties loadCombinedProperties() {
        final Properties props = new Properties();

        // Load default
        loadProperties(props, "application.properties");

        // Check for active profiles
        String activeProfiles = System.getProperty("spring.profiles.active");
        if (activeProfiles == null) {
            activeProfiles = props.getProperty("spring.profiles.active");
        }

        if (activeProfiles != null) {
            for (String profile : activeProfiles.split(",")) {
                loadProperties(props, "application-" + profile.trim() + ".properties");
            }
        }

        return props;
    }

    private void loadProperties(final Properties props, final String resourceName) {
        try (InputStream is = PropertyResolver.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {
        }
    }
}