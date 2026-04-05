package de.nihas101.midas.backup.service.snapshot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FilePropertiesLoader implements PropertiesLoader {

    @Override
    public Properties load() throws IOException {
        Properties props = new Properties();

        final Path propsPath = Paths.get("application.properties");
        if (!Files.exists(propsPath)) {
            // Nothing to do, because only defaults are used
            return null;
        }
        try (InputStream is = Files.newInputStream(propsPath)) {
            props.load(is);
        }
        return props;
    }
}
