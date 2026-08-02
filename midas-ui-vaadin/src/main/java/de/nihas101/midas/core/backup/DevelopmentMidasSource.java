package de.nihas101.midas.core.backup;

import de.nihas101.midas.core.backup.service.MidasSource;

import java.io.File;
import java.nio.file.Paths;

public class DevelopmentMidasSource implements MidasSource {

    @Override
    public File file() {
        return Paths.get("midas-ui-vaadin/target/midas.jar").toFile();
    }
}
