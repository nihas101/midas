package de.nihas101.midas.backup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class MidasExecutableNameResolver {

    private final MidasExecutableResolver midasExecutableResolver;

    public String getExecutableName() {
        return midasExecutableResolver.resolveExecutable()
                .map(File::getName)
                .map(name -> {
                    int lastDot = name.lastIndexOf('.');
                    return lastDot > 0 ? name.substring(0, lastDot) : name;
                })
                .orElse("midas");
    }

}
