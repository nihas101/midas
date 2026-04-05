package de.nihas101.midas.backup.service;

import de.nihas101.midas.MidasApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

@RequiredArgsConstructor
public class ProductionMidasSource implements MidasSource {

    private final File file;

    public ProductionMidasSource() {
        this(new ApplicationHome(MidasApplication.class).getSource());
    }

    @Override
    public File file() {
        if (file != null && file.isFile() && file.getName().endsWith(".jar")) {
            return file;
        } else {
            throw new RuntimeException("File '" + (file != null ? file.getAbsolutePath() : "null") + "' is not a jar");
        }
    }
}
