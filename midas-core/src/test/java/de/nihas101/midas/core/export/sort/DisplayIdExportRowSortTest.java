package de.nihas101.midas.core.export.sort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class DisplayIdExportRowSortTest {

    @ParameterizedTest
    @MethodSource("compareValues")
    void compare(SortableExportRow a, SortableExportRow b, int expected) {
        final DisplayIdExportRowSort displayIdExportRowSort = new DisplayIdExportRowSort();
        Assertions.assertEquals(expected, displayIdExportRowSort.compare(a, b));

    }

    public static Stream<Arguments> compareValues() {
        return Stream.of(
                Arguments.of(
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        0
                ),
                Arguments.of(
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(1, LocalDate.of(2026, 1, 2), 0),
                        -1
                ),
                Arguments.of(
                        new SortableTestRow(1, LocalDate.of(2026, 1, 2), 0),
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        1
                ),
                Arguments.of(
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(2, LocalDate.of(2026, 1, 1), 0),
                        -1
                ),
                Arguments.of(
                        new SortableTestRow(2, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        1
                ),
                Arguments.of(
                        new SortableTestRow(null, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        1
                ),
                Arguments.of(
                        new SortableTestRow(2, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(null, LocalDate.of(2026, 1, 1), 0),
                        -1
                ),
                Arguments.of(
                        new SortableTestRow(null, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(null, LocalDate.of(2026, 1, 1), 0),
                        0
                ),
                Arguments.of(
                        new SortableTestRow(1, null, 0),
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        1
                ),
                Arguments.of(
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        new SortableTestRow(1, null, 0),
                        -1
                ),
                Arguments.of(
                        new SortableTestRow(1, null, 0),
                        new SortableTestRow(1, null, 0),
                        0
                ),
                Arguments.of(null, null, 0),
                Arguments.of(
                        null,
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        1
                ),
                Arguments.of(
                        new SortableTestRow(1, LocalDate.of(2026, 1, 1), 0),
                        null,
                        -1
                )
        );
    }

    private record SortableTestRow(Integer shareholderId, LocalDate date, Integer id) implements SortableExportRow {
    }
}