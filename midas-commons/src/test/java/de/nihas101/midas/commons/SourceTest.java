package de.nihas101.midas.commons;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SourceTest {

    // Ensure all sources are covered
    @ParameterizedTest
    @EnumSource(Source.class)
    void fromString(Source input) {
        final Source output = Source.fromString(input.getSource());
        assertEquals(input, output);
    }

    // Ensure no change occurs for existing sources
    @ParameterizedTest
    @ValueSource(strings = {"user", "system"})
    void fromSourceString(String input) {
        final Source output = Source.fromString(input);
        assertNotNull(output);
    }
}