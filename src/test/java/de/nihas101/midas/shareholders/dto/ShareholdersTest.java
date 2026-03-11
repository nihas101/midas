package de.nihas101.midas.shareholders.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class ShareholdersTest {

    @ParameterizedTest
    @MethodSource("toListValues")
    void toList(Shareholders shareholders, List<Shareholder> expected) {
        final List<Shareholder> actual = shareholders.toList();
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }

    public static Stream<Arguments> toListValues() {
        return Stream.of(
                Arguments.of(new Shareholders(null), Collections.emptyList()),
                Arguments.of(new Shareholders(List.of(new Shareholder())), List.of(new Shareholder()))
        );
    }

    @Test
    void shareholdersIsImmutable() {
        final Shareholders shareholders = new Shareholders(Collections.emptyList());

        final List<Shareholder> list = shareholders.toList();
        list.add(new Shareholder());

        Assertions.assertEquals(0, shareholders.toList().size());
    }
}