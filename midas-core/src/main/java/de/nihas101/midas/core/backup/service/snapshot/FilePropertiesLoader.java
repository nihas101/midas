package de.nihas101.midas.core.backup.service.snapshot;

import de.nihas101.midas.api.backup.MidasFile;
import de.nihas101.midas.api.backup.MidasFileFactory;
import de.nihas101.midas.api.backup.PropertiesLoader;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

@RequiredArgsConstructor
public class FilePropertiesLoader implements PropertiesLoader {

    public final MidasFileFactory midasFileFactory;

    public FilePropertiesLoader() {
        this(new LocalMidasFileFactory());
    }

    @Override
    public Properties load() throws IOException {
        Properties props = new Properties();

        final MidasFile midasFile = midasFileFactory.create(Paths.get("application.properties"));
        if (!midasFile.exists()) {
            // Nothing to do, because only defaults are used
            return null;
        }
        try (InputStream is = midasFile.inputStream()) {
            props.load(is);
        }
        return props;
    }
}
