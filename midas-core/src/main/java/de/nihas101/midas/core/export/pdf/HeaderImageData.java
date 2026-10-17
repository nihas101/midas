package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.export.HeaderImageResolver;
import de.nihas101.midas.core.backup.service.MidasHeaderImageResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeaderImageData implements HeaderImageResolver {
    private final MidasHeaderImageResolver headerImageResolver;

    @Override
    public String dataUri() {
        final Optional<File> file = headerImageResolver.resolveHeaderImage();
        if (file.isEmpty()) {
            return null;
        }

        try {
            final ImageFile imageFile = loadImage(file.get());
            if (imageFile == null || imageFile.bytes() == null || imageFile.bytes().length == 0) {
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

    private ImageFile loadImage(final File file) throws IOException {
        final Path path = file.toPath();
        final Path fileName = path.getFileName();
        if (StringUtils.isBlank(fileName.toString())) {
            return null;
        }

        return new ImageFile(Files.readAllBytes(path), fileName.toString());
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