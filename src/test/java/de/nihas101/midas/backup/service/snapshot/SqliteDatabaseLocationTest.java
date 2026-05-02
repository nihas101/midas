package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class SqliteDatabaseLocationTest {

    @ParameterizedTest
    @MethodSource("databaseLocationArguments")
    void databaseLocation(
            final String datasourceUrl,
            final String expected
    ) {
        final SqliteDatabaseLocation location = new SqliteDatabaseLocation(datasourceUrl);
        Assertions.assertEquals(expected, location.databaseLocation());
    }

    public static Stream<Arguments> databaseLocationArguments() {
        return Stream.of(
                Arguments.of("jdbc:sqlite:midas.db", "midas.db"),
                Arguments.of("jdbc:sqlite:/absolute/path/to/data.db", "data.db"),
                Arguments.of(null, "midas.db"),
                Arguments.of("", "midas.db")
        );
    }
}
