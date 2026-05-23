package de.nihas101.midas.backup.service;

import de.nihas101.midas.export.pdf.config.PdfExportConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MidasTemplatesResolver {

    private final PdfExportConfig config;

    public Optional<File> resolveTemplateDirectory() {
        return Optional.ofNullable(config.getTemplatePath())
                .map(File::new)
                .filter(File::exists)
                .filter(File::isDirectory);
    }
}
