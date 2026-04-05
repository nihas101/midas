package de.nihas101.midas.backup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MidasExecutableResolver {

    private final MidasSource midasSource;

    public Optional<File> resolveExecutable() {
        try {
            return Optional.of(midasSource.file());
        } catch (RuntimeException e) {
            log.error("Failed to resolve executable", e);
            throw e;
        }
    }

}
