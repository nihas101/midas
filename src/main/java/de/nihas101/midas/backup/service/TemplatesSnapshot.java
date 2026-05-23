package de.nihas101.midas.backup.service;

import de.nihas101.midas.backup.service.snapshot.ArchiveWriter;
import de.nihas101.midas.backup.service.snapshot.Snapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TemplatesSnapshot implements Snapshot {

    private final ArchiveWriter archiveWriter;
    private final MidasTemplatesResolver templatesResolver;

    @Override
    public void create() throws IOException {
        final Optional<File> templateDirectory = templatesResolver.resolveTemplateDirectory();
        if (templateDirectory.isEmpty()) {
            log.debug("No template directory defined. Skipping template backup.");
            return;
        }

        final File templatesRoot = templateDirectory.get();
        if (!templatesRoot.isDirectory()) {
            log.warn("Template directory is not a directory. Skipping template backup.");
            return;
        }

        final File[] htmlTemplates = templatesRoot.listFiles(f -> f.getName().endsWith(".html"));
        if (htmlTemplates == null || htmlTemplates.length == 0) {
            log.warn("Template directory is empty. Skipping template backup.");
            return;
        }

        final String zipDirectory = "templates/";
        archiveWriter.addDirectory(zipDirectory);

        for (final File htmlTemplate : htmlTemplates) {
            log.info("Adding template {} to zip...", htmlTemplate.getName());
            archiveWriter.add(htmlTemplate, zipDirectory + htmlTemplate.getName());
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
