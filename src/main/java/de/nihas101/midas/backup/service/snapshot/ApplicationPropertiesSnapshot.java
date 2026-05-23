package de.nihas101.midas.backup.service.snapshot;

import de.nihas101.midas.backup.service.MidasTemplatesResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class ApplicationPropertiesSnapshot implements Snapshot {

    private final ArchiveWriter archiveWriter;
    private final DatabaseLocation databaseLocation;
    private final MidasTemplatesResolver templatesResolver;
    private final PropertiesLoader propertiesLoader;

    public ApplicationPropertiesSnapshot(
            final ArchiveWriter archiveWriter,
            final DatabaseLocation databaseLocation,
            final MidasTemplatesResolver templatesResolver
    ) {
        this(
                archiveWriter,
                databaseLocation,
                templatesResolver,
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

        correctDataSourceUrl(props);
        correctTemplatesDirectory(props);

        archiveWriter.add(props);
    }

    private void correctDataSourceUrl(final Properties props) {
        final String dbName = databaseLocation.databaseLocation();
        final String targetUrl = "jdbc:sqlite:" + dbName;
        if (!targetUrl.equals(props.getProperty("spring.datasource.url"))) {
            log.info("Adjusting spring.datasource.url to '{}' for portability", targetUrl);
            props.setProperty("spring.datasource.url", targetUrl);
        }
    }

    private void correctTemplatesDirectory(final Properties props) {
        final Optional<File> templateDirectory = templatesResolver.resolveTemplateDirectory();
        if (templateDirectory.isEmpty()) {
            return;
        }
        final String templatesDirectoryPath = "templates/";
        if (!templatesDirectoryPath.equals(props.getProperty("midas.export.pdf.template-path"))) {
            log.info("Adjusting midas.export.pdf.template-path to '{}' for portability", templatesDirectoryPath);
            props.setProperty("midas.export.pdf.template-path", templatesDirectoryPath);
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
