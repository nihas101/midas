package de.nihas101.midas.core.backup.service.snapshot;

import de.nihas101.midas.core.backup.service.MidasHeaderImageResolver;
import de.nihas101.midas.core.backup.service.MidasTemplatesResolver;
import de.nihas101.midas.persistance.backup.ArchiveWriter;
import de.nihas101.midas.persistance.backup.DatabaseLocation;
import de.nihas101.midas.persistance.backup.Snapshot;
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
    private final MidasHeaderImageResolver headerImageResolver;
    private final PropertiesLoader propertiesLoader;

    public ApplicationPropertiesSnapshot(
            final ArchiveWriter archiveWriter,
            final DatabaseLocation databaseLocation,
            final MidasTemplatesResolver templatesResolver,
            final MidasHeaderImageResolver headerImageResolver
    ) {
        this(
                archiveWriter,
                databaseLocation,
                templatesResolver,
                headerImageResolver,
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
        correctHeaderImagePath(props);

        archiveWriter.add(props);
    }

    private void correctDataSourceUrl(final Properties props) {
        final String targetUrl = databaseLocation.prefix() + databaseLocation.databaseLocation();
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

    private void correctHeaderImagePath(final Properties props) {
        if (headerImageResolver == null) {
            return;
        }
        final Optional<File> headerImage = headerImageResolver.resolveHeaderImage();
        if (headerImage.isEmpty()) {
            return;
        }
        final String imageFileName = headerImage.get().getName();
        if (!imageFileName.equals(props.getProperty("midas.export.pdf.header-image-path"))) {
            log.info("Adjusting midas.export.pdf.header-image-path to '{}' for portability", imageFileName);
            props.setProperty("midas.export.pdf.header-image-path", imageFileName);
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
