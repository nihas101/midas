package de.nihas101.midas;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@Push
@StyleSheet("midas-theme")
@EnableConfigurationProperties
public class MidasApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MidasApplication.class, args);
    }

}
