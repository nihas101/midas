package de.nihas101.midas.vaadin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyResolverTest {

    @Test
    void fromCommandLineNullParams() {
        final Optional<String> actual = new PropertyResolver<>(
                null,
                null,
                "DUMMY_1",
                a -> a
        ).fromCommandLine(new String[]{});

        assertTrue(actual.isEmpty());
    }

    @Test
    void fromCommandLineNullCommandLineArgs() {
        final Optional<String> actual = new PropertyResolver<>(
                null,
                emptyList(),
                "DUMMY_1",
                a -> a
        ).fromCommandLine(null);

        assertTrue(actual.isEmpty());
    }

    @MethodSource("fromCommandLineValues")
    @ParameterizedTest
    void fromCommandLine(
            final String[] args,
            final Boolean expectedValue
    ) {
        final Optional<Boolean> actual = new PropertyResolver<>(
                null,
                List.of("dummy1", "dummy2"),
                "DUMMY_1",
                Boolean::parseBoolean
        ).fromCommandLine(args);

        assertEquals(expectedValue != null, actual.isPresent());
        actual.ifPresent(b -> assertEquals(expectedValue, b));
    }

    public static Stream<Arguments> fromCommandLineValues() {
        return Stream.of(
                Arguments.of(new String[]{}, null),
                Arguments.of(new String[]{"--dummy1=false"}, false),
                Arguments.of(new String[]{"--nodummy1=false", "--dummy2=true"}, true),
                Arguments.of(new String[]{"--nodummy1=false", "--nodummy2=false"}, null)
        );
    }

    @Test
    void fromEnvironmentVariablesNullEnvVariable() {
        final PropertyLoader propertyLoader = Mockito.mock(PropertyLoader.class);
        final Optional<Boolean> actual = new PropertyResolver<>(
                propertyLoader,
                List.of("dummy1", "dummy2"),
                null,
                Boolean::parseBoolean
        ).fromEnvironmentVariables();

        assertFalse(actual.isPresent());
    }

    @MethodSource("fromEnvironmentVariablesValues")
    @ParameterizedTest
    void fromEnvironmentVariables(
            final String envVariableValue,
            final Boolean expectedValue
    ) {
        final EnvironmentVariableReader environmentVariableReader = Mockito.mock(EnvironmentVariableReader.class);
        Mockito.when(environmentVariableReader.getEnvironmentVariable("DUMMY_1")).thenReturn(envVariableValue);

        final Optional<Boolean> actual = new PropertyResolver<Boolean>(
                Mockito.mock(PropertyLoader.class),
                List.of("dummy1", "dummy2"),
                "DUMMY_1",
                Boolean::parseBoolean,
                environmentVariableReader
        ).fromEnvironmentVariables();

        assertEquals(expectedValue != null, actual.isPresent());
        actual.ifPresent(b -> assertEquals(expectedValue, b));
    }

    public static Stream<Arguments> fromEnvironmentVariablesValues() {
        return Stream.of(
                Arguments.of("true", true),
                Arguments.of(null, null)
        );
    }

    @MethodSource("fromPropertiesValues")
    @ParameterizedTest
    void fromProperties(
            final Properties properties,
            final Boolean expectedValue
    ) {
        final PropertyLoader propertyLoader = Mockito.mock(PropertyLoader.class);
        Mockito.when(propertyLoader.loadCombinedProperties()).thenReturn(properties);

        final Optional<Boolean> actual = new PropertyResolver<>(
                propertyLoader,
                List.of("dummy1", "dummy2"),
                "DUMMY_1",
                Boolean::parseBoolean
        ).fromProperties();

        assertEquals(expectedValue != null, actual.isPresent());
        actual.ifPresent(b -> assertEquals(expectedValue, b));
    }

    public static Stream<Arguments> fromPropertiesValues() {
        final Properties properties1 = new Properties();
        properties1.put("dummy1", "true");

        final Properties properties2 = new Properties();
        properties2.put("dummy1", "false");

        final Properties properties3 = new Properties();
        properties3.put("dummy2", "dummyValue2");

        final Properties properties4 = new Properties();
        properties4.put("dummy2", "true");

        return Stream.of(
                Arguments.of(properties1, true),
                Arguments.of(properties2, false),
                Arguments.of(properties3, false),
                Arguments.of(properties4, true)
        );
    }
}