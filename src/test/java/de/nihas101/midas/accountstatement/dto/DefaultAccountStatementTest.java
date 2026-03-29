package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Locale;
import java.util.stream.Stream;

class DefaultAccountStatementTest {

    @Test
    void nullTest() {
        final AccountStatement accountStatement = new DefaultAccountStatement(null);
        Assertions.assertNull(accountStatement.id());
        Assertions.assertNull(accountStatement.amount());
        Assertions.assertNull(accountStatement.date());
        Assertions.assertNull(accountStatement.label(null, null));
    }


    @ParameterizedTest
    @MethodSource("idArguments")
    void id(final Integer id) {
        final AccountStatement accountStatement = new DefaultAccountStatement(id, null, null, null);
        Assertions.assertEquals(id, accountStatement.id());
    }

    public static Stream<Arguments> idArguments() {
        return Stream.of(
                Arguments.of((Integer) null),
                Arguments.of(0),
                Arguments.of(-1),
                Arguments.of(1)
        );
    }

    @ParameterizedTest
    @MethodSource("dateArguments")
    void date(final Year year, LocalDate expectedDate) {
        final AccountStatement accountStatement = new DefaultAccountStatement(null, year, null, null);
        Assertions.assertEquals(expectedDate, accountStatement.date());
    }

    public static Stream<Arguments> dateArguments() {
        return Stream.of(
                Arguments.of(
                        null,
                        null
                ),
                Arguments.of(
                        Year.now(),
                        Year.now().atMonth(Month.DECEMBER).atEndOfMonth()
                ),
                Arguments.of(
                        Year.now().minusYears(1),
                        Year.now().minusYears(1).atMonth(Month.DECEMBER).atEndOfMonth())
                ,
                Arguments.of(
                        Year.now().plusYears(1),
                        Year.now().plusYears(1).atMonth(Month.DECEMBER).atEndOfMonth()
                )
        );
    }

    @ParameterizedTest
    @EnumSource(BookingType.class)
    void label(final BookingType bookingType) {
        final AccountStatement accountStatement = new DefaultAccountStatement(null, null, bookingType, null);
        final MessageSource messageSource = Mockito.mock(MessageSource.class);
        Mockito.when(messageSource.getMessage(bookingType.getAccountStatementI18nKey(), null, Locale.ENGLISH))
                .thenReturn("success");

        final String actualLabel = accountStatement.label(messageSource, Locale.ENGLISH);

        Assertions.assertEquals("success", actualLabel);
    }

    @ParameterizedTest
    @MethodSource("amountArguments")
    void amount(final MoneyAmount amount) {
        final AccountStatement accountStatement = new DefaultAccountStatement(null, null, null, amount);
        Assertions.assertEquals(amount, accountStatement.amount());
    }

    public static Stream<Arguments> amountArguments() {
        return Stream.of(
                Arguments.of((MoneyAmount) null),
                Arguments.of(MoneyAmount.ZERO),
                Arguments.of(MoneyAmount.ofCents(-10L)),
                Arguments.of(MoneyAmount.ofCents(10L))
        );
    }
}