package de.nihas101.midas.backup.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MidasExecutableResolverTest {

    private final MidasSource midasSource = mock(MidasSource.class);
    private final MidasExecutableResolver resolver = new MidasExecutableResolver(midasSource);

    @Test
    void resolveExecutable() {
        final File expectedFile = new File("test-app.jar");
        when(midasSource.file()).thenReturn(expectedFile);

        final Optional<File> result = resolver.resolveExecutable();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedFile, result.get());
    }

    @Test
    void resolveExecutableThrowsException() {
        when(midasSource.file()).thenThrow(new RuntimeException("Source failure"));

        Assertions.assertThrows(RuntimeException.class, resolver::resolveExecutable);
    }
}
