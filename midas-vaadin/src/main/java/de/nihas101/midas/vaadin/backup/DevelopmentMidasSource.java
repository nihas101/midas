package de.nihas101.midas.vaadin.backup;

import de.nihas101.midas.api.backup.MidasSource;

import java.io.File;
import java.nio.file.Paths;

public class DevelopmentMidasSource implements MidasSource {

    @Override
    public File file() {
        return Paths.get("midas-vaadin/target/midas-vaadin.jar").toFile();
    }
}
