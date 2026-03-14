package de.nihas101.midas;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Push
@Theme("midas-theme")
@EnableConfigurationProperties
public class MidasApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MidasApplication.class, args);
    }

}
