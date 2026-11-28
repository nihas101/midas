package de.nihas101.midas;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;
import de.nihas101.midas.core.browser.WebPage;
import de.nihas101.midas.vaadin.PropertyLoader;
import de.nihas101.midas.vaadin.PropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.BindException;
import java.util.List;

@Slf4j
@Push
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("styles.css")
@EnableScheduling
@SpringBootApplication(scanBasePackages = "de.nihas101.midas")
@EnableConfigurationProperties
public class MidasApplication implements AppShellConfigurator {

    public static void main(final String[] args) {
        try {
            SpringApplication.run(MidasApplication.class, args);
        } catch (Exception e) {
            if (isPortInUse(e)) {
                log.warn("Port is already in use. Assuming an instance is already running.", e);
                // Another instance is likely running, defer to it
                if (resolveLaunchBrowser(args)) {
                    openBrowser(args);
                }
            } else {
                throw e;
            }
        }
    }

    private static boolean isPortInUse(Throwable e) {
        while (e != null) {
            if (e instanceof BindException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    private static void openBrowser(final String[] args) {
        String port = resolvePort(args);
        String url = "http://localhost:" + port;
        log.info("Port {} is already in use. Opening browser at {}", port, url);
        try {
            new WebPage(url).open();
        } catch (Exception e) {
            log.error("Failed to open browser: {}", e.getMessage());
        }
    }

    static boolean resolveLaunchBrowser(final String[] args) {
        final PropertyResolver<Boolean> pr = new PropertyResolver<>(
                new PropertyLoader(),
                List.of("desktop.launch-browser", "desktop.launchBrowser"),
                "DESKTOP_LAUNCH_BROWSER",
                Boolean::parseBoolean
        );
        return pr.fromCommandLine(args).orElseGet(
                () -> pr.fromEnvironmentVariables().orElseGet(
                        () -> pr.fromProperties().orElse(true)));
    }

    private static String resolvePort(final String[] args) {
        final PropertyResolver<String> pr = new PropertyResolver<>(
                new PropertyLoader(),
                List.of("server.port"),
                "SERVER_PORT",
                a -> a
        );

        return pr.fromCommandLine(args).orElseGet(
                () -> pr.fromEnvironmentVariables().orElseGet(
                        () -> pr.fromProperties().orElse("8080")));
    }

}
