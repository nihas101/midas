package de.nihas101.midas.headless.backup;

import de.nihas101.midas.api.backup.MidasSource;

import java.io.File;
import java.nio.file.Paths;

public class DevelopmentMidasSource implements MidasSource {

    @Override
    public File file() {
        return Paths.get("midas-headless/target/midas-headless.jar").toFile();
    }
}
