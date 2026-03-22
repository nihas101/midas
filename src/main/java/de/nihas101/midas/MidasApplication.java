package de.nihas101.midas;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import de.nihas101.midas.browser.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Push
@Theme("midas-theme") // TODO: Update
@SpringBootApplication(scanBasePackages = "de.nihas101.midas")
@EnableConfigurationProperties
public class MidasApplication implements AppShellConfigurator {

    // TODO: Think about splitting this project into multiple modules
    //       sqlite persistence module
    //       persistance interface module
    //       business logic
    //       frontend (+ project that combines them all)

    public static void main(String[] args) {
        // TODO: Test this on windows to see if it works
        // TODO: Shut down application when no connection to a client is left
        // TODO: Add tooltips everywhere
        // TODO: Try importing dbf files
        // TODO: Add support for imports via some kind of (xml?) file that defines the inputs and mappings
        //       - dbf, csv
        // TODO: Add support for exports to: csv
        try {
            SpringApplication.run(MidasApplication.class, args);
        } catch (Exception e) {
            if (isPortInUse(e)) {
                // Another instance is likely running, defer to it
                openBrowser(args); // TODO: Also need to check if launchBrowser is enabled before trying this
            } else {
                throw e;
            }
        }
    }

    private static boolean isPortInUse(Throwable e) {
        while (e != null) {
            if (e instanceof java.net.BindException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    private static void openBrowser(String[] args) {
        String port = resolvePort(args);
        String url = "http://localhost:" + port;
        System.out.println("Port " + port + " is already in use. Opening browser at " + url);
        try {
            new WebPage(url).open();
        } catch (Exception e) {
            log.error("Failed to open browser: {}", e.getMessage());
        }
    }

    private static String resolvePort(String[] args) {
        // 1. Check command line arguments
        for (String arg : args) {
            if (arg.startsWith("--server.port=")) {
                return arg.substring("--server.port=".length());
            }
        }

        // 2. Check environment variables
        String envPort = System.getenv("SERVER_PORT");
        if (envPort != null) return envPort;

        // 3. Load from properties files
        Properties props = new Properties();

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

        return props.getProperty("server.port", "8080");
    }

    private static void loadProperties(Properties props, String resourceName) {
        try (InputStream is = MidasApplication.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {
        }
    }

}
