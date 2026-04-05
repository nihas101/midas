package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class ApplicationPropertiesSnapshot implements Snapshot {

    private final ArchiveWriter archiveWriter;
    private final DatabaseLocation databaseLocation;
    private final PropertiesLoader propertiesLoader;

    public ApplicationPropertiesSnapshot(
            final ArchiveWriter archiveWriter,
            final DatabaseLocation databaseLocation
    ) {
        this(
                archiveWriter,
                databaseLocation,
                new FilePropertiesLoader()
        );
    }

    @Override
    public void create() throws IOException {
        log.info("Adding application.properties to zip...");

        Properties props = propertiesLoader.load();
        if (props == null) {
            // Nothing to do, because only defaults are used
            return;
        }

        // Portability: Correct datasource URL if necessary
        String dbName = databaseLocation.databaseLocation();
        String targetUrl = "jdbc:sqlite:" + dbName;
        if (!targetUrl.equals(props.getProperty("spring.datasource.url"))) {
            log.info("Adjusting spring.datasource.url to {} for portability", targetUrl);
            props.setProperty("spring.datasource.url", targetUrl);
        }

        archiveWriter.add(props);
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
