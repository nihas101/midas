package de.nihas101.midas.core.accountstatement.dto;

import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.persistance.accountstatements.AccountStatementEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

class DefaultAccountStatementsTest {

    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final Year CURRENT_YEAR = Year.now();

    @Test
    void nullTests() {
        final AccountStatements accountStatement = new DefaultAccountStatements(
                null,
                null,
                (Year) null,
                null,
                null,
                null
        );
        Assertions.assertNotNull(accountStatement.forType(BookingType.WITHDRAWAL));
        Assertions.assertNull(accountStatement.openingBalance());
    }

    @ParameterizedTest
    @MethodSource("openingBalanceArguments")
    void openingBalance(final OpeningBalance openingBalance) {
        final AccountStatements accountStatement = new DefaultAccountStatements(
                null,
                null,
                null,
                openingBalance,
                null,
                null
        );
        Assertions.assertEquals(openingBalance, accountStatement.openingBalance());
    }

    public static Stream<Arguments> openingBalanceArguments() {
        return Stream.of(
                Arguments.of((OpeningBalance) null),
                Arguments.of(new DefaultOpeningBalance()),
                Arguments.of(new DefaultOpeningBalance(MoneyAmount.ZERO)),
                Arguments.of(new DefaultOpeningBalance(MoneyAmount.ofCents(10L))),
                Arguments.of(new DefaultOpeningBalance(MoneyAmount.ofCents(-10L)))
        );
    }

    @Test
    void forTypeNull() {
        final AccountStatements accountStatement = new DefaultAccountStatements(
                List.of(),
                null,
                bt -> new DefaultAccountStatement(
                        1,
                        CURRENT_YEAR,
                        bt,
                        MoneyAmount.ZERO,
                        null,
                        null
                ),
                null,
                null
        );
        Assertions.assertNull(accountStatement.forType(null));
    }

    @Test
    void forTypeNullMap() {
        final AccountStatements accountStatement = new DefaultAccountStatements(
                (Map<BookingType, LabeledAccountStatement>) null,
                null,
                bt -> new DefaultAccountStatement(
                        1,
                        CURRENT_YEAR,
                        bt,
                        MoneyAmount.ZERO,
                        null,
                        null
                )
        );
        Assertions.assertEquals(
                new DefaultAccountStatement(
                        1,
                        CURRENT_YEAR,
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ZERO,
                        null,
                        null
                ),
                accountStatement.forType(BookingType.WITHDRAWAL)
        );
    }

    @Test
    void forTypeNullMapAndNoFallback() {
        final AccountStatements accountStatement = new DefaultAccountStatements(
                (Map<BookingType, LabeledAccountStatement>) null,
                null,
                null
        );
        Assertions.assertNull(accountStatement.forType(BookingType.WITHDRAWAL));
    }

    @ParameterizedTest
    @MethodSource("forTypeArguments")
    void forType(
            final List<AccountStatementEntity> accountStatements,
            final BookingType bookingType,
            final Function<BookingType, LabeledAccountStatement> defaultsSupplier,
            final LabeledAccountStatement expectedAccountStatement
    ) {
        final AccountStatements accountStatement = new DefaultAccountStatements(
                accountStatements,
                null,
                defaultsSupplier,
                null,
                null
        );
        Assertions.assertEquals(expectedAccountStatement, accountStatement.forType(bookingType));
    }

    public static Stream<Arguments> forTypeArguments() {
        final Function<BookingType, LabeledAccountStatement> defaultsSupplier = bt -> new DefaultAccountStatement(
                1,
                CURRENT_YEAR,
                bt,
                MoneyAmount.ZERO,
                null,
                null
        );
        return Stream.of(
                Arguments.of(null, null, null, null),
                Arguments.of(
                        List.of(),
                        BookingType.WITHDRAWAL,
                        defaultsSupplier,
                        defaultsSupplier.apply(BookingType.WITHDRAWAL)
                ),
                Arguments.of(
                        List.of(new AccountStatementEntity()),
                        BookingType.WITHDRAWAL,
                        defaultsSupplier,
                        defaultsSupplier.apply(BookingType.WITHDRAWAL)
                ),
                Arguments.of(
                        List.of(
                                new AccountStatementEntity(
                                        1,
                                        CURRENT_DATE,
                                        BookingType.WITHDRAWAL,
                                        MoneyAmount.ofCents(10L)
                                )
                        ),
                        BookingType.COMPENSATION,
                        defaultsSupplier,
                        defaultsSupplier.apply(BookingType.COMPENSATION)
                ),
                Arguments.of(
                        List.of(
                                new AccountStatementEntity(
                                        1,
                                        CURRENT_DATE,
                                        BookingType.WITHDRAWAL,
                                        MoneyAmount.ofCents(10L)
                                )
                        ),
                        BookingType.WITHDRAWAL,
                        defaultsSupplier,
                        new DefaultAccountStatement(
                                1,
                                CURRENT_YEAR,
                                BookingType.WITHDRAWAL,
                                MoneyAmount.ofCents(10L),
                                null,
                                null
                        )
                ),
                Arguments.of(
                        List.of(
                                new AccountStatementEntity(
                                        1,
                                        CURRENT_DATE,
                                        BookingType.WITHDRAWAL,
                                        MoneyAmount.ofCents(10L)
                                ),
                                new AccountStatementEntity(
                                        2,
                                        CURRENT_DATE,
                                        BookingType.WITHDRAWAL,
                                        MoneyAmount.ofCents(10L)
                                )
                        ),
                        BookingType.WITHDRAWAL,
                        defaultsSupplier,
                        new DefaultAccountStatement(
                                1,
                                CURRENT_YEAR,
                                BookingType.WITHDRAWAL,
                                MoneyAmount.ofCents(10L),
                                null,
                                null
                        )
                )
        );
    }

    @Test
    void forAllTypes() {
        final AtomicInteger id = new AtomicInteger(1);
        final List<AccountStatementEntity> accountStatementEntities = Arrays.stream(BookingType.values())
                .map(bookingType -> {
                    final int currentId = id.getAndIncrement();
                    return new AccountStatementEntity(
                            currentId,
                            CURRENT_DATE,
                            bookingType,
                            MoneyAmount.ofCents(currentId * 100L)
                    );
                })
                .toList();

        final AccountStatements accountStatement = new DefaultAccountStatements(
                accountStatementEntities,
                null,
                CURRENT_YEAR,
                null,
                null,
                null
        );

        for (final BookingType bookingType : BookingType.values()) {
            final LabeledAccountStatement expected = accountStatementEntities.stream()
                    .filter(a -> bookingType == a.getType())
                    .findFirst()
                    .map(ase -> new DefaultAccountStatement(ase, null, null))
                    .orElseThrow();
            Assertions.assertEquals(expected, accountStatement.forType(bookingType));
        }
    }
}