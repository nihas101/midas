package de.nihas101.midas.backup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MidasExecutableResolver {

    private final MidasSource midasSource;

    @Autowired
    public MidasExecutableResolver() throws URISyntaxException {
        this(new ProductionMidasSource());
    }

    public Optional<File> resolveExecutable() {
        try {
            return Optional.of(midasSource.file());
        } catch (RuntimeException e) {
            log.error("Failed to resolve executable", e);
            throw e;
        }
    }

    // TODO: This can be a wrapper!
    public String getExecutableName() {
        return resolveExecutable()
                .map(File::getName)
                .map(name -> {
                    int lastDot = name.lastIndexOf('.');
                    return lastDot > 0 ? name.substring(0, lastDot) : name;
                })
                .orElse("midas");
    }

}
