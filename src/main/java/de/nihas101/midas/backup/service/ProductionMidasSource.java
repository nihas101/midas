package de.nihas101.midas.backup.service;

import de.nihas101.midas.MidasApplication;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.URISyntaxException;

@RequiredArgsConstructor
public class ProductionMidasSource implements MidasSource {

    private final File file;

    public ProductionMidasSource() throws URISyntaxException {
        this(new File(MidasApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
    }

    @Override
    public File file() {
        if (file.isFile() && file.getName().endsWith(".jar")) {
            return file;
        } else {
            throw new RuntimeException("File '" + file.getAbsolutePath() + "' is not a jar");
        }
    }
}
