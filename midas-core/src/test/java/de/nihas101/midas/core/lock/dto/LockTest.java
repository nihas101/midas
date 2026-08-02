package de.nihas101.midas.core.lock.dto;

import de.nihas101.midas.persistance.lock.LockEntity;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.stream.Stream;

class LockTest {

    @MethodSource("fromEntityValues")
    @ParameterizedTest
    void fromEntity(LockEntity input, Lock expected) {
        Assertions.assertEquals(expected, Lock.fromEntity(input));
    }

    public static Stream<Arguments> fromEntityValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(new LockEntity(), new Lock()),
                Arguments.of(new LockEntity(1, null, null), new Lock(1, null, null)),
                Arguments.of(new LockEntity(1, new ShareholderEntity(), null), new Lock(1, null, null)),
                Arguments.of(new LockEntity(1, new ShareholderEntity(2, 3, "first", "last"), null), new Lock(1, 2, null)),
                Arguments.of(new LockEntity(1, new ShareholderEntity(2, 3, "first", "last"), 2026), new Lock(1, 2, Year.of(2026)))
        );
    }
}