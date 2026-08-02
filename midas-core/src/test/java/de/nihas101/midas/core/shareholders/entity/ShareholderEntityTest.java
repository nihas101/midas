package de.nihas101.midas.core.shareholders.entity;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ShareholderEntityTest {

    @ParameterizedTest
    @MethodSource("fromDtoValues")
    void fromDto(Shareholder shareholder, ShareholderEntity expected) {
        Assertions.assertEquals(expected, ShareholderEntity.fromDto(shareholder));
    }

    public static Stream<Arguments> fromDtoValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(new DefaultShareholder(), new ShareholderEntity()),
                Arguments.of(
                        new DefaultShareholder(
                                1,
                                2,
                                "firstName",
                                "lastName"
                        ),
                        new ShareholderEntity(
                                1,
                                2,
                                "firstName",
                                "lastName"
                        )
                )
        );
    }
}