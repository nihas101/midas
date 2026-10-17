package de.nihas101.midas.core.backup.service;

import de.nihas101.midas.core.export.pdf.config.PdfExportConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MidasHeaderImageResolver {

    private final PdfExportConfig config;
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public Optional<File> resolveHeaderImage() {
        if (config == null) {
            return Optional.empty();
        }
        final String headerImagePath = config.getHeaderImagePath();
        if (headerImagePath == null || headerImagePath.isBlank()) {
            return Optional.empty();
        }

        try {
            final Path path = Path.of(headerImagePath);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return Optional.of(path.toFile());
            }

            final Resource resource = resourceLoader.getResource(headerImagePath);
            if (resource.exists() && resource.isFile()) {
                return Optional.of(resource.getFile());
            }
        } catch (IOException | RuntimeException e) {
            log.warn("Failed to resolve header image file for path '{}': {}", headerImagePath, e.getMessage());
        }

        return Optional.empty();
    }
}
