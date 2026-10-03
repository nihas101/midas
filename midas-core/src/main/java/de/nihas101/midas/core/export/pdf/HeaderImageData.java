package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.core.export.pdf.config.PdfExportConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeaderImageData implements HeaderImageResolver {
    private final PdfExportConfig pdfExportConfig;
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public String dataUri() {
        if (pdfExportConfig == null) {
            return null;
        }
        final String imagePath = pdfExportConfig.getHeaderImagePath();
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try {
            final ImageFile imageFile = loadImage(imagePath, imagePath);
            if (imageFile.bytes() == null || imageFile.bytes().length == 0) {
                log.warn("Header image is empty");
                return null;
            }

            final String mimeType = determineMimeType(imageFile.fileName());
            final String base64 = Base64.getEncoder().encodeToString(imageFile.bytes());
            return "data:" + mimeType + ";base64," + base64;
        } catch (Exception e) {
            log.warn("Failed to load header image", e);
            return null;
        }
    }

    private ImageFile loadImage(final String imagePath, String fileName) throws IOException {
        byte[] bytes = null;

        // Try direct file path first
        final Path path = Path.of(imagePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            bytes = Files.readAllBytes(path);
            fileName = path.getFileName().toString();
        } else {
            // Try via ResourceLoader (classpath or file protocol)
            final Resource resource = resourceLoader.getResource(imagePath);
            if (resource.exists() && resource.isReadable()) {
                try (final InputStream inputStream = resource.getInputStream()) {
                    bytes = inputStream.readAllBytes();
                    if (resource.getFilename() != null) {
                        fileName = resource.getFilename();
                    }
                }
            }
        }

        return new ImageFile(bytes, fileName);
    }

    private record ImageFile(byte[] bytes, String fileName) {
    }

    private String determineMimeType(final String fileName) {
        final String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lower.endsWith(".gif")) {
            return "image/gif";
        } else if (lower.endsWith(".bmp")) {
            return "image/bmp";
        }
        return "image/png";
    }
}