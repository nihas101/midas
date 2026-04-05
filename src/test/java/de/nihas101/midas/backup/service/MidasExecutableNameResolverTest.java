package de.nihas101.midas.backup.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MidasExecutableNameResolverTest {

    private final MidasExecutableResolver midasExecutableResolver = mock(MidasExecutableResolver.class);
    private final MidasExecutableNameResolver resolver = new MidasExecutableNameResolver(midasExecutableResolver);

    @Test
    void getExecutableNameFromJar() {
        final File file = new File("midas-v1.jar");
        when(midasExecutableResolver.resolveExecutable()).thenReturn(Optional.of(file));

        final String name = resolver.getExecutableName();

        Assertions.assertEquals("midas-v1", name);
    }

    @Test
    void getExecutableNameNoExtension() {
        final File file = new File("midas-standalone");
        when(midasExecutableResolver.resolveExecutable()).thenReturn(Optional.of(file));

        final String name = resolver.getExecutableName();

        Assertions.assertEquals("midas-standalone", name);
    }
}