package de.nihas101.midas.backup.service.snapshot;

import de.nihas101.midas.MidasApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class ApplicationPropertiesSnapshot implements Snapshot {

    private final ZipOutputStream zos; // TODO: Abstract away
    private final DatabaseLocation databaseLocation;

    @Override
    public void create() throws IOException {
        log.info("Adding application.properties to zip...");
        Properties props = new Properties();

        // Try to load from file system first, then classpath
        final Path propsPath = Paths.get("application.properties");
        if (Files.exists(propsPath)) {
            try (InputStream is = Files.newInputStream(propsPath)) {
                props.load(is);
            }
        } else {
            try (final InputStream is = MidasApplication.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (is != null) {
                    props.load(is);
                }
            }
        }

        // Portability: Correct datasource URL if necessary
        String dbName = databaseLocation.databaseLocation();
        String targetUrl = "jdbc:sqlite:" + dbName;
        if (!targetUrl.equals(props.getProperty("spring.datasource.url"))) {
            log.info("Adjusting spring.datasource.url to {} for portability", targetUrl);
            props.setProperty("spring.datasource.url", targetUrl);
        }

        zos.putNextEntry(new ZipEntry("application.properties"));
        props.store(zos, "Midas Backup Properties");
        zos.closeEntry();
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
