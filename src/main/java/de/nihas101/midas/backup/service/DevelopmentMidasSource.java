package de.nihas101.midas.backup.service;

import java.io.File;
import java.nio.file.Paths;

public class DevelopmentMidasSource implements MidasSource {

    @Override
    public File file() {
        return Paths.get("target/midas.jar").toFile();
    }
}
